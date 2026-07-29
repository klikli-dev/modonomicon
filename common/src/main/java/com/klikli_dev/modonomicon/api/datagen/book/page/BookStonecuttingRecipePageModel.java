/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookStonecuttingRecipePage;

public class BookStonecuttingRecipePageModel extends BookRecipePageModel<BookStonecuttingRecipePageModel> {
    protected BookStonecuttingRecipePageModel() {
        super(BookStonecuttingRecipePage.ID);
    }

    public static BookStonecuttingRecipePageModel create() {
        return new BookStonecuttingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookStonecuttingRecipePage(common);
    }
}
