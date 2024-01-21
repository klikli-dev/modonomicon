/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;

public class BookEmptyPageModel extends BookPageModel<BookEmptyPageModel> {

    protected BookEmptyPageModel() {
        super(Page.EMPTY);
    }

    public static BookEmptyPageModel create() {
        return new BookEmptyPageModel();
    }
}
