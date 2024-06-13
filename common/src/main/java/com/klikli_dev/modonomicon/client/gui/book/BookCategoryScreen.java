// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;

/**
 * A screen that represents a book. It usually manages other screens for categories and entries.
 */
public interface BookCategoryScreen {

    void onDisplay();

    void onClose();

    void loadState(CategoryVisualState state);

    void saveState(CategoryVisualState state);

    BookCategory getCategory();
}
