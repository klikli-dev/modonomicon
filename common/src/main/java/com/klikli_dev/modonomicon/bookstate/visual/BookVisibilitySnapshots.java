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

        for (BookCategory category : book.getCategories().values()) {
            if (!BookServices.visibility().isVisible(player, category)) {
                continue;
            }
            categories.add(category.getId());
            for (BookEntry entry : category.getEntries().values()) {
                if (!BookServices.visibility().isVisible(player, entry)) {
                    continue;
                }
                entries.add(entry.getId());
                pages.put(entry.getId(), BookServices.visibility().getVisiblePages(player, entry).stream().map(BookPage::getPageNumber).collect(Collectors.toSet()));
            }
        }

        return new BookVisibilitySnapshots(categories, entries, pages);
    }
}
