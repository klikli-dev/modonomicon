/*

 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class BookNotConditionModel extends BookConditionModel<BookNotConditionModel> {

    protected BookConditionModel<?> child;

    protected BookNotConditionModel() {
        super(Condition.NOT);
    }

    public static BookNotConditionModel create() {
        return new BookNotConditionModel();
    }

    public BookConditionModel<?> getChild() {
        return this.child;
    }

    @Override
    public JsonObject toJson(ResourceLocation conditionParentId, HolderLookup.Provider provider) {
        var json = super.toJson(conditionParentId, provider);
        json.add("child", this.child.toJson(conditionParentId, provider));
        return json;
    }

    public BookNotConditionModel withChild(BookConditionModel<?> child) {
        this.child = child;
        return this;
    }
}
