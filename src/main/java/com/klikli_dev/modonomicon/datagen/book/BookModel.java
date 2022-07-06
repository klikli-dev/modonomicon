/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Book;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookModel {
    protected ResourceLocation id;
    protected String name;
    protected String creativeTab = "misc";

    protected ResourceLocation model = new ResourceLocation(Book.DEFAULT_MODEL);
    protected ResourceLocation bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);
    protected ResourceLocation bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);
    protected int defaultTitleColor = 0x00000;
    protected List<BookCategoryModel> categories = new ArrayList<>();

    protected boolean autoAddReadConditions;

    public static Builder builder() {
        return new Builder();
    }

    public List<BookCategoryModel> getCategories() {
        return this.categories;
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
        json.addProperty("model", this.model.toString());
        json.addProperty("creative_tab", this.creativeTab);
        json.addProperty("book_overview_texture", this.bookOverviewTexture.toString());
        json.addProperty("book_content_texture", this.bookContentTexture.toString());
        json.addProperty("default_title_color", this.defaultTitleColor);
        json.addProperty("auto_add_read_conditions", this.autoAddReadConditions);
        return json;
    }

    public static final class Builder {
        protected ResourceLocation id;
        protected String name;

        protected String creativeTab = "misc";

        protected ResourceLocation model = new ResourceLocation(Book.DEFAULT_MODEL);
        protected ResourceLocation bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);
        protected ResourceLocation bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);
        protected int defaultTitleColor = 0x00000;
        protected List<BookCategoryModel> categories = new ArrayList<>();
        protected boolean autoAddReadConditions;

        private Builder() {
        }

        public static Builder aBookModel() {
            return new Builder();
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCreativeTab(String creativeTab) {
            this.creativeTab = creativeTab;
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

        public Builder withModel(ResourceLocation model) {
            this.model = model;
            return this;
        }


        public Builder withDefaultTitleColor(int defaultTitleColor) {
            this.defaultTitleColor = defaultTitleColor;
            return this;
        }

        public Builder withCategories(List<BookCategoryModel> categories) {
            this.categories = categories;
            return this;
        }

        public Builder withCategories(BookCategoryModel... categories) {
            this.categories.addAll(List.of(categories));
            return this;
        }

        public Builder withCategory(BookCategoryModel category) {
            this.categories.add(category);
            return this;
        }

        public Builder withAutoAddReadConditions(boolean autoAddReadConditions) {
            this.autoAddReadConditions = autoAddReadConditions;
            return this;
        }

        public BookModel build() {
            BookModel bookModel = new BookModel();
            for (var category : this.categories) {
                category.book = bookModel;
            }

            bookModel.categories = this.categories;
            bookModel.bookOverviewTexture = this.bookOverviewTexture;
            bookModel.id = this.id;
            bookModel.bookContentTexture = this.bookContentTexture;
            bookModel.defaultTitleColor = this.defaultTitleColor;
            bookModel.name = this.name;
            bookModel.model = this.model;
            bookModel.autoAddReadConditions = this.autoAddReadConditions;
            bookModel.creativeTab = this.creativeTab;
            return bookModel;
        }
    }
}
