/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookOrCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.Arrays;

public class BookOrConditionModel extends BookConditionModel<BookOrConditionModel> {

    protected BookConditionModel<?>[] children;

    protected BookOrConditionModel() {
        super(BookOrCondition.ID);
    }

    public static BookOrConditionModel create() {
        return new BookOrConditionModel();
    }

    public BookConditionModel<?>[] getChildren() {
        return this.children;
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookOrCondition(this.tooltipComponent(), Arrays.stream(this.children)
                .map(child -> child.toBookCondition(provider))
                .toArray(BookCondition[]::new));
    }

    public BookOrConditionModel withChildren(BookConditionModel<?>... children) {
        this.children = children;
        return this;
    }
}
