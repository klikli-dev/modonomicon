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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.datagen.book.page.BookPageModel;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookEntryModel {
    protected ResourceLocation id;
    protected BookCategoryModel category;
    protected List<BookEntryParentModel> parents = new ArrayList<>();
    protected String name;
    protected String description = "";
    protected String icon;
    protected int x;
    protected int y;
    protected List<BookPageModel> pages = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("category", this.category.id.toString());
        json.addProperty("name", this.name);
        json.addProperty("description", this.description);
        json.addProperty("icon", this.icon);
        json.addProperty("x", this.x);
        json.addProperty("y", this.y);
        var pagesArray = new JsonArray();
        for (BookPageModel page : this.pages) {
            pagesArray.add(page.toJson());
        }
        json.add("pages", pagesArray);
        return json;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BookCategoryModel getCategory() {
        return this.category;
    }

    public List<BookEntryParentModel> getParents() {
        return this.parents;
    }

    public void addParent(BookEntryParentModel parent) {
        this.parents.add(parent);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIcon() {
        return this.icon;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public List<BookPageModel> getPages() {
        return this.pages;
    }

    public static final class Builder {
        protected ResourceLocation id;
        protected BookCategoryModel category;
        protected List<BookEntryParentModel> parents = new ArrayList<>();
        protected String name;
        protected String description = "";
        protected String icon;
        protected int x;
        protected int y;
        protected List<BookPageModel> pages = new ArrayList<>();

        private Builder() {
        }

        public static Builder aBookEntryModel() {
            return new Builder();
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withCategory(BookCategoryModel category) {
            this.category = category;
            return this;
        }

        public Builder withParents(List<BookEntryParentModel> parents) {
            this.parents = parents;
            return this;
        }

        public Builder withParents(BookEntryParentModel ... parents) {
            this.parents.addAll(List.of(parents));
            return this;
        }

        public Builder withParent(BookEntryParentModel parent) {
            this.parents.add(parent);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withX(int x) {
            this.x = x;
            return this;
        }

        public Builder withY(int y) {
            this.y = y;
            return this;
        }

        public Builder withPages(List<BookPageModel> pages) {
            this.pages = pages;
            return this;
        }

        public Builder withPages(BookPageModel ... pages) {
            this.pages.addAll(List.of(pages));
            return this;
        }

        public Builder withPage(BookPageModel page) {
            this.pages.add(page);
            return this;
        }

        public BookEntryModel build() {
            BookEntryModel bookEntryModel = new BookEntryModel();
            bookEntryModel.description = this.description;
            bookEntryModel.category = this.category;
            bookEntryModel.name = this.name;
            bookEntryModel.icon = this.icon;
            bookEntryModel.x = this.x;
            bookEntryModel.parents = this.parents;
            bookEntryModel.y = this.y;
            bookEntryModel.id = this.id;
            bookEntryModel.pages = this.pages;
            return bookEntryModel;
        }
    }
}
