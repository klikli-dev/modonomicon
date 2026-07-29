/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookBlastingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookBlastingRecipePageModel extends BookRecipePageModel<BookBlastingRecipePageModel> {
    protected BookBlastingRecipePageModel() {
        super(BookBlastingRecipePage.ID);
    }

    public static BookBlastingRecipePageModel create() {
        return new BookBlastingRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookBlastingRecipePage(common);
    }
}
