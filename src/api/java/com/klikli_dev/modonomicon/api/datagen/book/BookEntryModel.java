/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

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
    protected int entryBackgroundUIndex = 0;
    protected int entryBackgroundVIndex = 0;

    protected boolean hideWhileLocked;
    protected List<BookPageModel> pages = new ArrayList<>();
    protected BookConditionModel condition;

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
        json.addProperty("background_u_index", this.entryBackgroundUIndex);
        json.addProperty("background_v_index", this.entryBackgroundVIndex);
        json.addProperty("hide_while_locked", this.hideWhileLocked);

        if(!this.parents.isEmpty()){
            var parentsArray = new JsonArray();
            for (var parent : this.parents) {
                parentsArray.add(parent.toJson());
            }
            json.add("parents", parentsArray);
        }

        if(!this.pages.isEmpty()) {
            var pagesArray = new JsonArray();
            for (var page : this.pages) {
                pagesArray.add(page.toJson());
            }
            json.add("pages", pagesArray);
        }


        if(this.condition != null) {
            json.add("condition", this.condition.toJson());
        }

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

    public boolean isHideWhileLocked() {
        return this.hideWhileLocked;
    }

    public List<BookPageModel> getPages() {
        return this.pages;
    }

    public static final class Builder {
        public ResourceLocation id;
        public BookCategoryModel category;
        public List<BookEntryParentModel> parents = new ArrayList<>();
        public String name;
        public String description = "";
        public String icon;
        public int x;
        public int y;

        protected int entryBackgroundUIndex = 0;
        protected int entryBackgroundVIndex = 0;

        public boolean hideWhileLocked;
        public List<BookPageModel> pages = new ArrayList<>();
        public BookConditionModel condition;

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

        public Builder withParents(BookEntryParentModel... parents) {
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

        public Builder withLocation(Vec2 location) {
            return this.withX((int)location.x).withY((int)location.y);
        }

        public Builder withLocation(int x, int y) {
            return this.withX(x).withY(y);
        }


        public Builder withX(int x) {
            this.x = x;
            return this;
        }

        public Builder withY(int y) {
            this.y = y;
            return this;
        }

        /**
         * U = Y Axis / Up-Down
         * V = X Axis / Left-Right
         */
        public Builder withEntryBackground(int u, int v) {
            this.entryBackgroundUIndex = u;
            this.entryBackgroundVIndex = v;
            return this;
        }

        public Builder hideWhileLocked(boolean hideWhileLocked) {
            this.hideWhileLocked = hideWhileLocked;
            return this;
        }

        public Builder withPages(List<BookPageModel> pages) {
            this.pages = pages;
            return this;
        }

        public Builder withPages(BookPageModel... pages) {
            this.pages.addAll(List.of(pages));
            return this;
        }

        public Builder withPage(BookPageModel page) {
            this.pages.add(page);
            return this;
        }

        public Builder withCondition(BookConditionModel condition) {
            this.condition = condition;
            return this;
        }

        public BookEntryModel build() {
            BookEntryModel bookEntryModel = new BookEntryModel();
            bookEntryModel.description = this.description;
            bookEntryModel.category = this.category;
            bookEntryModel.name = this.name;
            bookEntryModel.icon = this.icon;
            bookEntryModel.x = this.x;
            bookEntryModel.y = this.y;
            bookEntryModel.entryBackgroundUIndex= this.entryBackgroundUIndex;
            bookEntryModel.entryBackgroundVIndex = this.entryBackgroundVIndex;
            bookEntryModel.hideWhileLocked = this.hideWhileLocked;
            bookEntryModel.parents = this.parents;
            bookEntryModel.id = this.id;
            bookEntryModel.pages = this.pages;
            bookEntryModel.condition = this.condition;
            return bookEntryModel;
        }
    }
}
