/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions.context;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;

public class BookConditionEntryContext extends BookConditionContext {
    public final BookEntry entry;

    public BookConditionEntryContext(Book book, BookEntry entry) {
        super(book);
        this.entry = entry;
    }

    public BookEntry getEntry() {
        return this.entry;
    }
}
