/*
 *
 *  * SPDX-FileCopyrightText: 2024 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;

public class BookNoneConditionModel extends BookConditionModel<BookNoneConditionModel> {
    protected BookNoneConditionModel() {
        super(Condition.NONE);
    }

    public static BookNoneConditionModel create() {
        return new BookNoneConditionModel();
    }
}
