/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookFalseCondition;
import net.minecraft.core.HolderLookup;

public class BookFalseConditionModel extends BookConditionModel<BookFalseConditionModel> {
    protected BookFalseConditionModel() {
        super(BookFalseCondition.ID);
    }

    public static BookFalseConditionModel create() {
        return new BookFalseConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookFalseCondition(this.tooltipComponent());
    }
}
