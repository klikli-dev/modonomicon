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
import net.minecraft.resources.ResourceLocation;

public class BookEntryUnlockedConditionModel extends BookConditionModel {
    protected String entryId;

    protected BookEntryUnlockedConditionModel(String entryId, Component tooltip, String tooltipString) {
        super(Condition.ENTRY_UNLOCKED, tooltip, tooltipString);
        this.entryId = entryId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEntryId() {
        return this.entryId;
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("entry_id", this.entryId);
        return json;
    }

    public static final class Builder {
        private String entryId;
        private Component tooltip;
        private String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
        }

        public String getEntryId() {
            return this.entryId;
        }

        public Component getTooltip() {
            return this.tooltip;
        }

        public String getTooltipString() {
            return this.tooltipString;
        }

        public Builder withEntry(ResourceLocation entryId) {
            this.entryId = entryId.toString();
            return this;
        }

        public Builder withEntry(String entryId) {
            this.entryId = entryId;
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


        public BookEntryUnlockedConditionModel build() {
            BookEntryUnlockedConditionModel model = new BookEntryUnlockedConditionModel(this.entryId, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
