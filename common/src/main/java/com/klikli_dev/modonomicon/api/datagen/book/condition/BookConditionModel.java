/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

public abstract class BookConditionModel<T extends BookConditionModel<T>> {
    protected Component tooltip = null;
    protected String tooltipString = null;

    protected Identifier type;

    protected BookConditionModel(Identifier type) {
        this.type = type;
    }

    public Identifier getType() {
        return this.type;
    }

    public Component getTooltip() {
        return this.tooltip;
    }

    public String getTooltipString() {
        return this.tooltipString;
    }

    public JsonObject toJson(Identifier conditionParentId, HolderLookup.Provider provider) {
        return BookCondition.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.toBookCondition(provider))
                .getOrThrow(JsonParseException::new)
                .getAsJsonObject();
    }

    protected Component tooltipComponent() {
        if (this.tooltip != null) {
            return this.tooltip;
        }
        return this.tooltipString != null ? Component.translatable(this.tooltipString) : null;
    }

    public abstract BookCondition toBookCondition(HolderLookup.Provider provider);

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
