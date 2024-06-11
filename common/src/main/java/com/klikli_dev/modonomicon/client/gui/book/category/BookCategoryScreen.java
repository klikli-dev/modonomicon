// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.category;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;

/**
 * A screen that represents a book. It usually manages other screens for categories and entries.
 */
public interface BookCategoryScreen {

    void onClose();

    void onCloseEntry(BookEntryScreen screen);
}
