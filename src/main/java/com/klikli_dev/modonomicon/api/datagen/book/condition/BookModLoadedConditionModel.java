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
import net.minecraft.network.chat.Component;

public class BookModLoadedConditionModel extends BookConditionModel {
    private final String modId;

    protected BookModLoadedConditionModel(String modId, Component tooltip, String tooltipString) {
        super(Condition.MOD_LOADED, tooltip, tooltipString);
        this.modId = modId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("mod_id", this.modId);
        return json;
    }

    public String getModId() {
        return this.modId;
    }

    public static final class Builder {
        private String modId;
        private Component tooltip;
        private String tooltipString;

        private Builder() {
        }

        public Builder withModId(String advancementId) {
            this.modId = advancementId;
            return this;
        }

        public Builder withTooltip(Component tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Will overwrite withTooltip
         */
        public Builder withTooltipString(String tooltipString) {
            this.tooltipString = tooltipString;
            return this;
        }

        public String getModId() {
            return this.modId;
        }

        public Component getTooltip() {
            return this.tooltip;
        }

        public String getTooltipString() {
            return this.tooltipString;
        }

        public BookModLoadedConditionModel build() {
            BookModLoadedConditionModel model = new BookModLoadedConditionModel(this.modId, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
