/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import net.minecraft.network.chat.Component;

public class BookAndConditionModel extends BookConditionModel {

    protected BookConditionModel[] children;

    protected BookAndConditionModel(BookConditionModel[] children, Component tooltip, String tooltipString) {
        super(Condition.AND, tooltip, tooltipString);
        this.children = children;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        var children = new JsonArray();
        for (var child : this.children) {
            children.add(child.toJson());
        }
        json.add("children", children);
        return json;
    }

    public static final class Builder {
        protected BookConditionModel[] children;
        protected Component tooltip;
        protected String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
        }

        public Builder withChildren(BookConditionModel... children) {
            this.children = children;
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

        public BookAndConditionModel build() {
            BookAndConditionModel model = new BookAndConditionModel(this.children, this.tooltip, this.tooltipString);
            return model;
        }
    }
}
