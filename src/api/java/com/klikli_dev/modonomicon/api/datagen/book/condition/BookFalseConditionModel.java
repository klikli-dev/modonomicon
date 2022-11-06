/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import net.minecraft.network.chat.Component;

public class BookFalseConditionModel extends BookConditionModel {
    protected BookFalseConditionModel(Component tooltip, String tooltipString) {
        super(Condition.FALSE, tooltip, tooltipString);
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


        public BookFalseConditionModel build() {
            BookFalseConditionModel model = new BookFalseConditionModel(this.tooltip, this.tooltipString);
            return model;
        }
    }
}
