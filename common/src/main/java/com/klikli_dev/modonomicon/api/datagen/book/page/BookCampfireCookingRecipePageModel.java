/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookCampfireCookingRecipePageModel extends BookRecipePageModel<BookCampfireCookingRecipePageModel> {
    protected BookCampfireCookingRecipePageModel() {
        super(Page.CAMPFIRE_COOKING_RECIPE);
    }

    public static BookCampfireCookingRecipePageModel create() {
        return new BookCampfireCookingRecipePageModel();
    }
}
