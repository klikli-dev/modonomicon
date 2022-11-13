/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions.context;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;

public class BookConditionCategoryContext extends BookConditionContext {
    public final BookCategory category;

    public BookConditionCategoryContext(Book book, BookCategory category) {
        super(book);
        this.category = category;
    }

    @Override
    public String toString() {
        return "BookConditionCategoryContext{" +
                "book=" + this.book +
                ", category=" + this.category +
                '}';
    }

    public BookCategory getCategory() {
        return this.category;
    }
}
