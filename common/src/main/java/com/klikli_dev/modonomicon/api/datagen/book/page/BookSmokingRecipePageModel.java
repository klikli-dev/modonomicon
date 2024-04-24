/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookSmokingRecipePageModel extends BookRecipePageModel<BookSmokingRecipePageModel> {
    protected BookSmokingRecipePageModel() {
        super(Page.SMOKING_RECIPE);
    }

    public static BookSmokingRecipePageModel create() {
        return new BookSmokingRecipePageModel();
    }
}
