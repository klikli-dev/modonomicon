/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookPageModel {

    protected ResourceLocation type;
    protected String anchor;
    protected BookConditionModel condition;

    protected BookPageModel(ResourceLocation type, @NotNull String anchor, @NotNull BookConditionModel condition) {
        this.type = type;
        this.anchor = anchor;
        this.condition = condition;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public BookConditionModel getCondition() {
        return this.condition;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.toString());
        json.addProperty("anchor", this.anchor);
        json.add("condition", this.condition.toJson());
        return json;
    }
}
