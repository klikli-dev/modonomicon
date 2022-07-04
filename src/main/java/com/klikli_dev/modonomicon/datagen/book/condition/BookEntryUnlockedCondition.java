/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.datagen.book.condition.BookEntryReadCondition.Builder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookEntryUnlockedCondition extends BookConditionModel {
    protected String entryId;

    protected BookEntryUnlockedCondition(String entryId, Component tooltip, String tooltipString) {
        super(Condition.ENTRY_UNLOCKED, tooltip, tooltipString);
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


        public BookEntryUnlockedCondition build() {
            BookEntryUnlockedCondition model = new BookEntryUnlockedCondition(this.entryId, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
