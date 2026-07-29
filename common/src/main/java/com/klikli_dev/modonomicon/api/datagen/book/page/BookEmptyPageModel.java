/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookEmptyPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

public class BookEmptyPageModel extends BookPageModel<BookEmptyPageModel> {

    protected BookEmptyPageModel() {
        super(BookEmptyPage.ID);
    }

    public static BookEmptyPageModel create() {
        return new BookEmptyPageModel();
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookEmptyPage(this.id, this.condition(provider));
    }
}
