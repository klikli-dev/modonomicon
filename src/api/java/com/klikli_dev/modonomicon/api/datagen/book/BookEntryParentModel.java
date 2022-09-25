/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class BookEntryParentModel {
    protected ResourceLocation entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;

    public static Builder builder() {
        return new Builder();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("entry", this.entryId.toString());
        json.addProperty("draw_arrow", this.drawArrow);
        json.addProperty("line_enabled", this.lineEnabled);
        json.addProperty("line_reversed", this.lineReversed);
        return json;
    }

    public ResourceLocation getEntryId() {
        return this.entryId;
    }

    public boolean isDrawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }

    public static final class Builder {
        protected ResourceLocation entryId;
        protected boolean drawArrow = true;
        protected boolean lineEnabled = true;
        protected boolean lineReversed = false;

        private Builder() {
        }

        public static Builder aBookEntryParentModel() {
            return new Builder();
        }

        public Builder withEntryId(ResourceLocation entryId) {
            this.entryId = entryId;
            return this;
        }

        public Builder withDrawArrow(boolean drawArrow) {
            this.drawArrow = drawArrow;
            return this;
        }

        public Builder withLineEnabled(boolean lineEnabled) {
            this.lineEnabled = lineEnabled;
            return this;
        }

        public Builder withLineReversed(boolean lineReversed) {
            this.lineReversed = lineReversed;
            return this;
        }

        public BookEntryParentModel build() {
            BookEntryParentModel bookEntryParentModel = new BookEntryParentModel();
            bookEntryParentModel.entryId = this.entryId;
            bookEntryParentModel.drawArrow = this.drawArrow;
            bookEntryParentModel.lineEnabled = this.lineEnabled;
            bookEntryParentModel.lineReversed = this.lineReversed;
            return bookEntryParentModel;
        }
    }
}
