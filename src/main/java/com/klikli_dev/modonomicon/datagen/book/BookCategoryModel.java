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
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Category;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookCategoryModel {
    protected BookModel book;
    protected ResourceLocation id;
    protected String name;
    protected String icon;
    protected int sortNumber = -1;
    protected ResourceLocation background = new ResourceLocation(Category.DEFAULT_BACKGROUND);
    protected ResourceLocation entryTextures = new ResourceLocation(Category.DEFAULT_ENTRY_TEXTURES);
    protected List<BookEntryModel> entries = new ArrayList<>();

    private BookCategoryModel(){
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<BookEntryModel> getEntries() {
        return this.entries;
    }

    public BookModel getBook() {
        return this.book;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("icon", this.icon);
        json.addProperty("sort_number", this.sortNumber);
        json.addProperty("background", this.background.toString());
        json.addProperty("entry_textures", this.entryTextures.toString());
        return json;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public ResourceLocation getEntryTextures() {
        return this.entryTextures;
    }

    public static final class Builder {
        protected BookModel book;
        protected ResourceLocation id;
        protected String name;
        protected String icon;
        protected int sortNumber = -1;
        protected ResourceLocation background = new ResourceLocation(Category.DEFAULT_BACKGROUND);
        protected ResourceLocation entryTextures = new ResourceLocation(Category.DEFAULT_ENTRY_TEXTURES);
        protected List<BookEntryModel> entries = new ArrayList<>();

        private Builder() {
        }

        public static Builder aBookCategoryModel() {
            return new Builder();
        }

        public Builder withBook(BookModel book) {
            this.book = book;
            return this;
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withSortNumber(int sortNumber) {
            this.sortNumber = sortNumber;
            return this;
        }

        public Builder withBackground(ResourceLocation background) {
            this.background = background;
            return this;
        }

        public Builder withEntryTextures(ResourceLocation entryTextures) {
            this.entryTextures = entryTextures;
            return this;
        }

        public Builder withEntries(List<BookEntryModel> entries) {
            this.entries = entries;
            return this;
        }

        public Builder withEntries(BookEntryModel... entries) {
            this.entries.addAll(List.of(entries));
            return this;
        }

        public Builder withEntry(BookEntryModel entry) {
            this.entries.add(entry);
            return this;
        }

        public BookCategoryModel build() {
            BookCategoryModel bookCategoryModel = new BookCategoryModel();
            for(var entry : this.entries){
                entry.category = bookCategoryModel;
                if(!entry.id.getPath().startsWith(this.id.getPath())){
                    entry.id = new ResourceLocation(entry.id.getNamespace(), this.id.getPath() + "/" + entry.id.getPath());
                }
            }

            bookCategoryModel.book = this.book;
            bookCategoryModel.icon = this.icon;
            bookCategoryModel.entryTextures = this.entryTextures;
            bookCategoryModel.background = this.background;
            bookCategoryModel.id = this.id;
            bookCategoryModel.entries = this.entries;
            bookCategoryModel.name = this.name;
            bookCategoryModel.sortNumber = this.sortNumber;

            return bookCategoryModel;
        }
    }
}
