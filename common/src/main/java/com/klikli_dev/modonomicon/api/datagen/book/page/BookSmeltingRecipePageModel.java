/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSmeltingRecipePage;

public class BookSmeltingRecipePageModel extends BookRecipePageModel<BookSmeltingRecipePageModel> {
    protected BookSmeltingRecipePageModel() {
        super(BookSmeltingRecipePage.ID);
    }

    public static BookSmeltingRecipePageModel create() {
        return new BookSmeltingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookSmeltingRecipePage(common);
    }
}
