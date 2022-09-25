/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookConditionModel {
    protected Component tooltip;
    protected String tooltipString;

    protected ResourceLocation type;

    protected BookConditionModel(ResourceLocation type, Component tooltip, String tooltipString) {
        this.type = type;
        this.tooltip = tooltip;
        this.tooltipString = tooltipString;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.getType().toString());
        if (this.tooltipString != null)
            json.addProperty("tooltip", this.tooltipString);
        if (this.tooltip != null)
            json.addProperty("tooltip", Component.Serializer.toJson(this.tooltip));
        return json;
    }

    public ResourceLocation getType() {
        return this.type;
    }
}
