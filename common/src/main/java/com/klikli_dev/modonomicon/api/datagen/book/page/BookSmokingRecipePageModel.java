/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSmokingRecipePage;

public class BookSmokingRecipePageModel extends BookRecipePageModel<BookSmokingRecipePageModel> {
    protected BookSmokingRecipePageModel() {
        super(BookSmokingRecipePage.ID);
    }

    public static BookSmokingRecipePageModel create() {
        return new BookSmokingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookSmokingRecipePage(common);
    }
}
