/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookCraftingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookCraftingRecipePageModel extends BookRecipePageModel<BookCraftingRecipePageModel> {
    protected BookCraftingRecipePageModel() {
        super(BookCraftingRecipePage.ID);
    }

    public static BookCraftingRecipePageModel create() {
        return new BookCraftingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookCraftingRecipePage(common);
    }
}
