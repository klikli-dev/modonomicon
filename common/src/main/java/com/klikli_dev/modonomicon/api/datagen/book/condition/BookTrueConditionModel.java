/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookTrueCondition;
import net.minecraft.core.HolderLookup;

public class BookTrueConditionModel extends BookConditionModel<BookTrueConditionModel> {
    protected BookTrueConditionModel() {
        super(BookTrueCondition.ID);
    }

    public static BookTrueConditionModel create() {
        return new BookTrueConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookTrueCondition(this.tooltipComponent());
    }
}
