/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookStonecuttingRecipePageModel extends BookRecipePageModel<BookStonecuttingRecipePageModel> {
    protected BookStonecuttingRecipePageModel() {
        super(Page.STONECUTTING_RECIPE);
    }

    public static BookStonecuttingRecipePageModel create() {
        return new BookStonecuttingRecipePageModel();
    }
}
