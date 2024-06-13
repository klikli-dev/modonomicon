/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an address in a book, consisting of the book, category, entry and page.
 * Used to navigate to a specific page in a book and to store such a state
 */
public record BookAddress(@NotNull ResourceLocation bookId,
                          ResourceLocation categoryId, boolean ignoreSavedCategory,
                          ResourceLocation entryId, boolean ignoreSavedEntry,
                          int page, boolean ignoreSavedPage
) {
    public static BookAddress defaultFor(@NotNull BookCategory category) {
        return of(category.getBook().getId(), category.getId(), null, -1);
    }

    public static BookAddress defaultFor(@NotNull BookEntry entry) {
        return of(entry.getBook().getId(), entry.getCategory().getId(), entry.getId(), -1);
    }

    public static BookAddress defaultFor(@NotNull Book book) {
        return defaultFor(book.getId());
    }

    public static BookAddress defaultFor(@NotNull ResourceLocation bookId) {
        return of(bookId, null, null, -1);
    }

    public static BookAddress of(@NotNull ResourceLocation bookId,
                                         ResourceLocation categoryId,
                                         ResourceLocation entryId,
                                         int page) {
        return new BookAddress(bookId, categoryId, false, entryId, false, page, false);
    }

    public static BookAddress ignoreSaved(@NotNull ResourceLocation bookId,
                                 ResourceLocation categoryId,
                                 ResourceLocation entryId,
                                 int page) {
        return new BookAddress(bookId, categoryId, true, entryId, true, page, true);
    }
}