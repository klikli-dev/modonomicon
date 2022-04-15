/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.datagen.book;

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
