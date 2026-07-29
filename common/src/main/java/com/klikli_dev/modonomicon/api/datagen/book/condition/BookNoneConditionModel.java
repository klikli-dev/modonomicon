/*
 *
 *  * SPDX-FileCopyrightText: 2024 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import net.minecraft.core.HolderLookup;

public class BookNoneConditionModel extends BookConditionModel<BookNoneConditionModel> {
    protected BookNoneConditionModel() {
        super(BookNoneCondition.ID);
    }

    public static BookNoneConditionModel create() {
        return new BookNoneConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookNoneCondition(this.tooltipComponent());
    }
}
