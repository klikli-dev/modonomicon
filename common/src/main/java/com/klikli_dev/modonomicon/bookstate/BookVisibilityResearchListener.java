/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.state.ResearchStateChangeListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Marks entries and categories as unread when a research batch makes them visible.
 * Registered in {@code Modonomicon.init()}.
 */
public final class BookVisibilityResearchListener implements ResearchStateChangeListener {

    public static final BookVisibilityResearchListener INSTANCE = new BookVisibilityResearchListener();

    private BookVisibilityResearchListener() {
    }

    @Override
    public Completion beforeChange(ServerPlayer player) {
        //If books are not built yet (e.g. right after /reload before datapack sync), build them lazily.
        //Otherwise visibility snapshots below would run against unlinked books and fail.
        if (!BookDataManager.get().areBooksBuilt()) {
            try {
                BookDataManager.get().tryBuildBooks(player.level());
            } catch (Exception e) {
                Modonomicon.LOG.error("Failed to lazily build books before applying research hooks, skipping visibility updates to avoid a crash.", e);
            }
        }

        var before = BookDataManager.get().getBooks().values().stream()
                .collect(Collectors.toMap(Book::getId, book -> collectSnapshot(player, book)));

        return changed -> {
            if (changed) {
                updateUnread(player, before);
            }
        };
    }

    private static void updateUnread(ServerPlayer player, Map<Identifier, BookVisibilitySnapshots> before) {
        for (var book : BookDataManager.get().getBooks().values()) {
            if (book.hasBuildFailed()) {
                continue;
            }
            try {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            } catch (Exception e) {
                //One broken book must never crash a server tick (see #385).
                Modonomicon.LOG.error("Failed to update visibility-driven unread state for book '{}', skipping to avoid a crash.", book.getId(), e);
            }
        }
    }

    private static BookVisibilitySnapshots collectSnapshot(ServerPlayer player, Book book) {
        try {
            return BookVisibilitySnapshots.collect(player, book);
        } catch (Exception e) {
            Modonomicon.LOG.error("Failed to collect visibility snapshot for book '{}', using empty snapshot to avoid a crash.", book.getId(), e);
            return new BookVisibilitySnapshots(Set.of(), Set.of(), Map.of());
        }
    }
}
