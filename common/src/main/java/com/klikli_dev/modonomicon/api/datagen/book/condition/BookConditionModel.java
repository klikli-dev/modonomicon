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

public class BookConditionModel<T extends BookConditionModel<T>> {
    protected Component tooltip = null;
    protected String tooltipString = null;

    protected ResourceLocation type;

    protected BookConditionModel(ResourceLocation type) {
        this.type = type;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public Component getTooltip() {
        return this.tooltip;
    }

    public String getTooltipString() {
        return this.tooltipString;
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

    public T withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Will overwrite withTooltip
     */
    public T withTooltipString(String tooltipString) {
        this.tooltipString = tooltipString;
        //noinspection unchecked
        return (T) this;
    }
}
