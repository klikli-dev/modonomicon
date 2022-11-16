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

public class BookAdvancementConditionModel extends BookConditionModel {
    private final String advancementId;

    protected BookAdvancementConditionModel(String advancementId, Component tooltip, String tooltipString) {
        super(Condition.ADVANCEMENT, tooltip, tooltipString);
        this.advancementId = advancementId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("advancement_id", this.advancementId);
        return json;
    }

    public String getAdvancementId() {
        return this.advancementId;
    }

    public static final class Builder {
        private String advancementId;
        private Component tooltip;
        private String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
        }

        public Builder withAdvancementId(String advancementId) {
            this.advancementId = advancementId;
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

        public String getAdvancementId() {
            return this.advancementId;
        }

        public Component getTooltip() {
            return this.tooltip;
        }

        public String getTooltipString() {
            return this.tooltipString;
        }

        public BookAdvancementConditionModel build() {
            BookAdvancementConditionModel model = new BookAdvancementConditionModel(this.advancementId, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
