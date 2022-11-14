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

public class BookEntryReadConditionModel extends BookConditionModel {
    protected String entryId;

    protected BookEntryReadConditionModel(String entryId, Component tooltip, String tooltipString) {
        super(Condition.ENTRY_READ, tooltip, tooltipString);
        this.entryId = entryId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("entry_id", this.entryId);
        return json;
    }

    public static final class Builder {
        protected String entryId;
        protected Component tooltip;
        protected String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
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


        public BookEntryReadConditionModel build() {
            BookEntryReadConditionModel model = new BookEntryReadConditionModel(this.entryId, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
