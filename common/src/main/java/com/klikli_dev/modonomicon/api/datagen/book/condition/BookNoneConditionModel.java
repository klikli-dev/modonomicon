package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.network.chat.Component;

public class BookNoneConditionModel extends BookConditionModel {
    public BookNoneConditionModel() {
        this(null, null);
    }

    protected BookNoneConditionModel(Component tooltip, String tooltipString) {
        super(ModonomiconConstants.Data.Condition.NONE, tooltip, tooltipString);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Component tooltip;
        private String tooltipString;

        private Builder() {
        }

        public static Builder aBookAdvancementConditionModel() {
            return new Builder();
        }

        public Component getTooltip() {
            return this.tooltip;
        }

        public String getTooltipString() {
            return this.tooltipString;
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


        public BookNoneConditionModel build() {
            BookNoneConditionModel model = new BookNoneConditionModel(this.tooltip, this.tooltipString);
            return model;
        }
    }
}
