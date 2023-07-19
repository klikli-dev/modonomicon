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
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookAdvancementConditionModel extends BookConditionModel {
    private final ResourceLocation advancementId;

    protected BookAdvancementConditionModel(ResourceLocation advancementId, Component tooltip, String tooltipString) {
        super(Condition.ADVANCEMENT, tooltip, tooltipString);
        this.advancementId = advancementId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("advancement_id", this.advancementId.toString());
        return json;
    }

    public ResourceLocation getAdvancementId() {
        return this.advancementId;
    }


    public static final class Builder {
        private ResourceLocation advancementId;
        private Component tooltip;
        private String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
        }

        public Builder withAdvancementId(ResourceLocation advancementId) {
            this.advancementId = advancementId;
            return this;
        }

        public Builder withAdvancementId(String advancementId) {
            this.advancementId = new ResourceLocation(advancementId);
            return this;
        }

        public Builder withAdvancement(Advancement advancement) {
            this.advancementId = advancement.getId();
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

        public ResourceLocation getAdvancementId() {
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
