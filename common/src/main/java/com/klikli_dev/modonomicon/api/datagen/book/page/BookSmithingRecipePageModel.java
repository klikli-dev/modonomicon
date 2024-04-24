/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookSmithingRecipePageModel extends BookRecipePageModel<BookSmithingRecipePageModel> {
    protected BookSmithingRecipePageModel() {
        super(Page.SMITHING_RECIPE);
    }

    public static BookSmithingRecipePageModel create() {
        return new BookSmithingRecipePageModel();
    }
}
