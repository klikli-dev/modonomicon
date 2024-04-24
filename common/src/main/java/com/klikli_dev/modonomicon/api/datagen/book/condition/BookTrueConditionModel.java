/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;

public class BookTrueConditionModel extends BookConditionModel<BookTrueConditionModel> {
    protected BookTrueConditionModel() {
        super(Condition.TRUE);
    }

    public static BookTrueConditionModel create() {
        return new BookTrueConditionModel();
    }
}
