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
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.Arrays;

public class BookAndConditionModel extends BookConditionModel<BookAndConditionModel> {

    protected BookConditionModel<?>[] children;

    protected BookAndConditionModel() {
        super(BookAndCondition.ID);
    }

    public static BookAndConditionModel create() {
        return new BookAndConditionModel();
    }

    public BookConditionModel<?>[] getChildren() {
        return this.children;
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookAndCondition(this.tooltipComponent(), Arrays.stream(this.children)
                .map(child -> child.toBookCondition(provider))
                .toArray(BookCondition[]::new));
    }

    public BookAndConditionModel withChildren(BookConditionModel<?>... children) {
        this.children = children;
        return this;
    }
}
