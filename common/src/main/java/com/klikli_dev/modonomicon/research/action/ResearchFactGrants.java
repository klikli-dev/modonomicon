/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.stream.Collectors;

/**
 * Grants a research fact directly, outside of the actions attached to facts. Used by the grant fact command and by the
 * client request for players with game master permission (see the grant research fact message).
 * <p>
 * Callers validate the fact and the permission of the player. The grant runs through the state change pipeline, then the
 * visibility driven unread state of the books and the research and book visual states are synced to the player.
 */
public final class ResearchFactGrants {

    private ResearchFactGrants() {
    }

    /**
     * Grants the fact to the player.
     *
     * @return true if the fact was not granted before and is granted now
     */
    public static boolean grant(ServerPlayer player, Identifier factId) {
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));

        boolean changed = ResearchServices.hooks().applyStateChange(player, () -> ResearchServices.state().grantFact(player, factId));

        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }

        ResearchServices.state().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
        return changed;
    }
}
