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
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookOrConditionModel extends BookConditionModel<BookOrConditionModel> {

    protected BookConditionModel<?>[] children;

    protected BookOrConditionModel() {
        super(Condition.OR);
    }

    public static BookOrConditionModel create() {
        return new BookOrConditionModel();
    }

    public BookConditionModel<?>[] getChildren() {
        return this.children;
    }

    @Override
    public JsonObject toJson(Identifier conditionParentId, HolderLookup.Provider provider) {
        var json = super.toJson(conditionParentId, provider);
        var children = new JsonArray();
        for (var child : this.children) {
            children.add(child.toJson(conditionParentId, provider));
        }
        json.add("children", children);
        return json;
    }

    public BookOrConditionModel withChildren(BookConditionModel<?>... children) {
        this.children = children;
        return this;
    }
}
