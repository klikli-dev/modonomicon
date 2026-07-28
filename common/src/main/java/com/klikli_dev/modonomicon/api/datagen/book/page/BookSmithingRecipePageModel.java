/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;

public class BookSmithingRecipePageModel extends BookRecipePageModel<BookSmithingRecipePageModel> {
    protected BookSmithingRecipePageModel() {
        super(BookSmithingRecipePage.ID);
    }

    public static BookSmithingRecipePageModel create() {
        return new BookSmithingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookSmithingRecipePage(common);
    }
}
