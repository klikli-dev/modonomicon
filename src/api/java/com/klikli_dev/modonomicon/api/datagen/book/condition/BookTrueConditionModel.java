/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import net.minecraft.network.chat.Component;

public class BookTrueConditionModel extends BookConditionModel {
    protected BookTrueConditionModel(Component tooltip, String tooltipString) {
        super(Condition.TRUE, tooltip, tooltipString);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        protected Component tooltip;
        protected String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
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


        public BookTrueConditionModel build() {
            BookTrueConditionModel model = new BookTrueConditionModel(this.tooltip, this.tooltipString);
            return model;
        }
    }
}
