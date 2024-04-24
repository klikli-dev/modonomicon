/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookSmeltingRecipePageModel extends BookRecipePageModel<BookSmeltingRecipePageModel> {
    protected BookSmeltingRecipePageModel() {
        super(Page.SMELTING_RECIPE);
    }

    public static BookSmeltingRecipePageModel create() {
        return new BookSmeltingRecipePageModel();
    }
}
