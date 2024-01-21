/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookBlastingRecipePageModel extends BookRecipePageModel<BookBlastingRecipePageModel> {
    protected BookBlastingRecipePageModel() {
        super(Page.BLASTING_RECIPE);
    }

    public static BookBlastingRecipePageModel create() {
        return new BookBlastingRecipePageModel();
    }
}
