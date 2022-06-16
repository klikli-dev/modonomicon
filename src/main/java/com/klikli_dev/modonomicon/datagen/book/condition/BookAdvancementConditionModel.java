/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.datagen.book.page.BookMultiblockPageModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookAdvancementConditionModel extends BookConditionModel {
    protected String advancementId;

    protected BookAdvancementConditionModel(String advancementId, Component tooltip, String tooltipString) {
        super(Condition.ADVANCEMENT, tooltip, tooltipString);
        this.advancementId = advancementId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        protected String advancementId;
        protected Component tooltip;
        protected String tooltipString;
        protected ResourceLocation type;

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

        public Builder withType(ResourceLocation type) {
            this.type = type;
            return this;
        }

        public BookAdvancementConditionModel build() {
            BookAdvancementConditionModel bookAdvancementConditionModel = new BookAdvancementConditionModel(advancementId, tooltip, tooltipString);
            bookAdvancementConditionModel.type = this.type;
            return bookAdvancementConditionModel;
        }
    }
}
