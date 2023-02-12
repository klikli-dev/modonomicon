/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Book;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookModel {
    protected ResourceLocation id;
    protected String name;
    protected String tooltip;
    protected String creativeTab = "modonomicon:modonomicon";

    protected ResourceLocation model = new ResourceLocation(Book.DEFAULT_MODEL);
    protected ResourceLocation bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);
    protected ResourceLocation bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);

    protected ResourceLocation craftingTexture = new ResourceLocation(Book.DEFAULT_CRAFTING_TEXTURE);
    protected int defaultTitleColor = 0x00000;
    protected List<BookCategoryModel> categories = new ArrayList<>();

    protected boolean autoAddReadConditions;
    protected boolean generateBookItem = true;
    protected ResourceLocation customBookItem;

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth;

    public static Builder builder() {
        return new Builder();
    }

    public boolean isAutoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public boolean isGenerateBookItem() {
        return this.generateBookItem;
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    public ResourceLocation getCustomBookItem() {
        return this.customBookItem;
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

    public String getTooltip() {
        return this.tooltip;
    }

    public String getCreativeTab() {
        return this.creativeTab;
    }

    public ResourceLocation getModel() {
        return this.model;
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

    public int getBookTextOffsetX() {
        return this.bookTextOffsetX;
    }

    public int getBookTextOffsetY() {
        return this.bookTextOffsetY;
    }

    public int getBookTextOffsetWidth() {
        return this.bookTextOffsetWidth;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("tooltip", this.tooltip);
        json.addProperty("model", this.model.toString());
        json.addProperty("creative_tab", this.creativeTab);
        json.addProperty("book_overview_texture", this.bookOverviewTexture.toString());
        json.addProperty("book_content_texture", this.bookContentTexture.toString());
        json.addProperty("crafting_texture", this.craftingTexture.toString());
        json.addProperty("default_title_color", this.defaultTitleColor);
        json.addProperty("book_text_offset_x", this.bookTextOffsetX);
        json.addProperty("book_text_offset_y", this.bookTextOffsetY);
        json.addProperty("book_text_offset_width", this.bookTextOffsetWidth);
        json.addProperty("auto_add_read_conditions", this.autoAddReadConditions);
        json.addProperty("generate_book_item", this.generateBookItem);
        if (this.customBookItem != null) {
            json.addProperty("custom_book_item", this.customBookItem.toString());
        }
        return json;
    }

    public static final class Builder {
        private ResourceLocation id;
        private String name;
        private String tooltip;

        private String creativeTab = "modonomicon:modonomicon";

        private ResourceLocation model = new ResourceLocation(Book.DEFAULT_MODEL);
        private ResourceLocation bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);
        private ResourceLocation bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);

        private ResourceLocation craftingTexture = new ResourceLocation(Book.DEFAULT_CRAFTING_TEXTURE);
        private int defaultTitleColor = 0x00000;
        private List<BookCategoryModel> categories = new ArrayList<>();
        private boolean autoAddReadConditions;

        private boolean generateBookItem = true;
        private ResourceLocation customBookItem;

        /**
         * When rendering book text holders, add this offset to the x position (basically, create a left margin).
         * Will be automatically subtracted from the width to avoid overflow.
         */
        protected int bookTextOffsetX;

        /**
         * When rendering book text holders, add this offset to the y position (basically, create a top margin).
         */
        protected int bookTextOffsetY;

        /**
         * When rendering book text holders, add this offset to the width (allows to create a right margin)
         * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
         */
        protected int bookTextOffsetWidth;

        private Builder() {
        }

        public static Builder aBookModel() {
            return new Builder();
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public String getCreativeTab() {
            return this.creativeTab;
        }

        public ResourceLocation getModel() {
            return this.model;
        }

        public ResourceLocation getBookOverviewTexture() {
            return this.bookOverviewTexture;
        }

        public ResourceLocation getBookContentTexture() {
            return this.bookContentTexture;
        }

        public ResourceLocation getCraftingTexture() {
            return this.craftingTexture;
        }

        public int getDefaultTitleColor() {
            return this.defaultTitleColor;
        }

        public List<BookCategoryModel> getCategories() {
            return this.categories;
        }

        public boolean isAutoAddReadConditions() {
            return this.autoAddReadConditions;
        }

        public boolean isGenerateBookItem() {
            return this.generateBookItem;
        }

        public ResourceLocation getCustomBookItem() {
            return this.customBookItem;
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTooltip(String tooltip) {
            this.tooltip = tooltip;
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

        public Builder withCraftingTexture(ResourceLocation craftingTexture) {
            this.craftingTexture = craftingTexture;
            return this;
        }

        public Builder withModel(ResourceLocation model) {
            this.model = model;
            return this;
        }

        public Builder withGenerateBookItem(boolean generateBookItem) {
            this.generateBookItem = generateBookItem;
            return this;
        }

        public Builder withCustomBookItem(ResourceLocation customBookItem) {
            this.customBookItem = customBookItem;
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

        /**
         * When rendering book text holders, add this offset to the x position (basically, create a left margin).
         * Will be automatically subtracted from the width to avoid overflow.
         */
        public Builder withBookTextOffsetX(int bookTextOffsetX) {
            this.bookTextOffsetX = bookTextOffsetX;
            return this;
        }

        /**
         * When rendering book text holders, add this offset to the y position (basically, create a top margin).
         */
        public Builder withBookTextOffsetY(int bookTextOffsetY) {
            this.bookTextOffsetY = bookTextOffsetY;
            return this;
        }

        /**
         * When rendering book text holders, add this offset to the width (allows to create a right margin)
         * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
         */
        public Builder withBookTextOffsetWidth(int bookTextOffsetWidth) {
            this.bookTextOffsetWidth = bookTextOffsetWidth;
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
            bookModel.craftingTexture = this.craftingTexture;
            bookModel.defaultTitleColor = this.defaultTitleColor;
            bookModel.name = this.name;
            bookModel.tooltip = this.tooltip;
            bookModel.model = this.model;
            bookModel.generateBookItem = this.generateBookItem;
            bookModel.customBookItem = this.customBookItem;
            bookModel.autoAddReadConditions = this.autoAddReadConditions;
            bookModel.creativeTab = this.creativeTab;
            bookModel.bookTextOffsetX = this.bookTextOffsetX;
            bookModel.bookTextOffsetY = this.bookTextOffsetY;
            bookModel.bookTextOffsetWidth = this.bookTextOffsetWidth;
            return bookModel;
        }
    }
}
