/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions.context;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;

public abstract class BookConditionContext {
    public final Book book;

    public BookConditionContext(Book book) {
        this.book = book;
    }

    public static BookConditionContext of(Book book, BookCategory category) {
        return new BookConditionCategoryContext(book, category);
    }

    public static BookConditionContext of(Book book, BookEntry entry) {
        return new BookConditionEntryContext(book, entry);
    }

    public static BookConditionContext of(Book book, BookPage page) {
        return new BookConditionPageContext(book, page);
    }

    public Book getBook() {
        return this.book;
    }

    @Override
    public String toString() {
        return "BookConditionContext{" +
                "book=" + this.book +
                '}';
    }
}
