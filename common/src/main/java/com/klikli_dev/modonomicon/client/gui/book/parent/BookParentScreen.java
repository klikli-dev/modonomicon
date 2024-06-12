// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.parent;

import com.klikli_dev.modonomicon.book.BookProvider;
import com.klikli_dev.modonomicon.client.gui.book.category.BookCategoryScreen;

/**
 * A screen that represents a book. It usually manages other screens for categories and entries.
 */
public interface BookParentScreen extends BookProvider {

    BookCategoryScreen getCurrentCategoryScreen();

    void onDisplay();

    /**
     * This is provided by any vanilla screen, and usually overridden by Modonomicon screens.
     * Making it available in this interface allows various book child screens to close the entire book.
     */
    void onClose();
}
