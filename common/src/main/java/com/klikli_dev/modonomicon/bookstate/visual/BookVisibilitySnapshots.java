/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visual;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BookVisibilitySnapshots(Set<Identifier> categories, Set<Identifier> entries, Map<Identifier, Set<Integer>> pages) {

    public static BookVisibilitySnapshots collect(Player player, Book book) {
        var categories = new HashSet<Identifier>();
        var entries = new HashSet<Identifier>();
        var pages = new HashMap<Identifier, Set<Integer>>();

        if (book == null) {
            Modonomicon.LOG.error("Tried to collect visibility snapshots for null book. Skipping to avoid a crash.");
            return new BookVisibilitySnapshots(categories, entries, pages);
        }

        for (BookCategory category : book.getCategories().values()) {
            boolean categoryVisible;
            try {
                categoryVisible = BookServices.visibility().isVisible(player, category);
            } catch (Exception e) {
                //One broken category condition must never crash a server tick (see #385).
                Modonomicon.LOG.error("Failed to check visibility for category '{}' in book '{}', hiding category to avoid a crash.", category.getId(), book.getId(), e);
                continue;
            }
            if (!categoryVisible) {
                continue;
            }
            categories.add(category.getId());
            for (BookEntry entry : category.getEntries().values()) {
                boolean entryVisible;
                try {
                    entryVisible = BookServices.visibility().isVisible(player, entry);
                } catch (Exception e) {
                    Modonomicon.LOG.error("Failed to check visibility for entry '{}' in book '{}', hiding entry to avoid a crash.", entry.getId(), book.getId(), e);
                    continue;
                }
                if (!entryVisible) {
                    continue;
                }
                entries.add(entry.getId());
                try {
                    pages.put(entry.getId(), BookServices.visibility().getVisiblePages(player, entry).stream().map(BookPage::getPageNumber).collect(Collectors.toSet()));
                } catch (Exception e) {
                    Modonomicon.LOG.error("Failed to collect visible pages for entry '{}' in book '{}', skipping pages to avoid a crash.", entry.getId(), book.getId(), e);
                }
            }
        }

        return new BookVisibilitySnapshots(categories, entries, pages);
    }
}
