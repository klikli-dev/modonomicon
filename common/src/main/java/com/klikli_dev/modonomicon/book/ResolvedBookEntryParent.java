/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.klikli_dev.modonomicon.book.entries.BookEntry;

public class ResolvedBookEntryParent extends BookEntryParent {
    protected BookEntry entry;

    public ResolvedBookEntryParent(BookEntryParent unresolved, BookEntry entry) {
        super(entry.getId());
        this.entry = entry;
        this.drawArrow = unresolved.drawArrow;
        this.lineEnabled = unresolved.lineEnabled;
        this.lineReversed = unresolved.lineReversed;
    }

    @Override
    public BookEntry getEntry() {
        return this.entry;
    }
}
