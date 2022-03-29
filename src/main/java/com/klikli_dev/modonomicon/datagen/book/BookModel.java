/*
 * LGPL-3-0
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
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import net.minecraft.resources.ResourceLocation;

public class BookModel {
    protected ResourceLocation id;
    protected String name;
    protected ResourceLocation bookOverviewTexture;
    protected ResourceLocation bookContentTexture;
    protected int defaultTitleColor;

    public static Builder builder() {
        return new Builder();
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("book_overview_texture", this.bookOverviewTexture.toString());
        json.addProperty("book_content_texture", this.bookContentTexture.toString());
        json.addProperty("defaultTitleColor", this.defaultTitleColor);
        return json;
    }

    public static final class Builder {
        protected ResourceLocation id;
        protected String name;
        protected ResourceLocation bookOverviewTexture;
        protected ResourceLocation bookContentTexture;
        protected int defaultTitleColor;

        private Builder() {
            this.bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);
            this.bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);
            this.defaultTitleColor = 0x00000;
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBookOverviewTexture(ResourceLocation bookOverviewTexture) {
            this.bookOverviewTexture = bookOverviewTexture;
            return this;
        }

        public Builder withBookContentTexture(ResourceLocation bookContentTexture) {
            this.bookContentTexture = bookContentTexture;
            return this;
        }

        public Builder withDefaultTitleColor(int defaultTitleColor) {
            this.defaultTitleColor = defaultTitleColor;
            return this;
        }

        public BookModel build() {
            BookModel bookModel = new BookModel();
            bookModel.name = this.name;
            bookModel.bookOverviewTexture = this.bookOverviewTexture;
            bookModel.defaultTitleColor = this.defaultTitleColor;
            bookModel.bookContentTexture = this.bookContentTexture;
            bookModel.id = this.id;
            return bookModel;
        }
    }
}
