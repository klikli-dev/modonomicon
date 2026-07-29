/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookCampfireCookingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookCampfireCookingRecipePageModel extends BookRecipePageModel<BookCampfireCookingRecipePageModel> {
    protected BookCampfireCookingRecipePageModel() {
        super(BookCampfireCookingRecipePage.ID);
    }

    public static BookCampfireCookingRecipePageModel create() {
        return new BookCampfireCookingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookCampfireCookingRecipePage(common);
    }
}
