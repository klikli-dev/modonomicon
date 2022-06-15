/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions.context;

import com.klikli_dev.modonomicon.book.Book;

public class BookConditionContext {
    public final Book book;

    public BookConditionContext(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return this.book;
    }
}
