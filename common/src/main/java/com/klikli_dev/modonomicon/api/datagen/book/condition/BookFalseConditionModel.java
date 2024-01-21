/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;

public class BookFalseConditionModel extends BookConditionModel<BookFalseConditionModel> {
    protected BookFalseConditionModel() {
        super(Condition.FALSE);
    }

    public static BookFalseConditionModel create() {
        return new BookFalseConditionModel();
    }
}
