/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookCraftingRecipePageModel extends BookRecipePageModel<BookCraftingRecipePageModel> {
    protected BookCraftingRecipePageModel() {
        super(Page.CRAFTING_RECIPE);
    }

    public static BookCraftingRecipePageModel create() {
        return new BookCraftingRecipePageModel();
    }
}
