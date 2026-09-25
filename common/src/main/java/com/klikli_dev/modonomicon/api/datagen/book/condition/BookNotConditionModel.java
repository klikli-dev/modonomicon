/*
 *
 *  * SPDX-FileCopyrightText: 2026 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNotCondition;
import net.minecraft.core.HolderLookup;

public class BookNotConditionModel extends BookConditionModel<BookNotConditionModel> {

    protected BookConditionModel<?> child;

    protected BookNotConditionModel() {
        super(BookNotCondition.ID);
    }

    public static BookNotConditionModel create() {
        return new BookNotConditionModel();
    }

    public BookConditionModel<?> getChild() {
        return this.child;
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookNotCondition(this.tooltipComponent(), this.child.toBookCondition(provider));
    }

    public BookNotConditionModel withChild(BookConditionModel<?> child) {
        this.child = child;
        return this;
    }
}
