/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Category;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

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

    protected BookConditionModel condition;
    protected boolean showCategoryButton = true;

    private BookCategoryModel() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public BookConditionModel getCondition() {
        return this.condition;
    }

    public boolean isShowCategoryButton() {
        return this.showCategoryButton;
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
        if (this.condition != null) {
            json.add("condition", this.condition.toJson());
        }
        json.addProperty("show_category_button", this.showCategoryButton);
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
        private final List<BookEntryModel> entries = new ArrayList<>();
        private BookModel book;
        private ResourceLocation id;
        private String name;
        private String icon;
        private int sortNumber = -1;
        private ResourceLocation background = new ResourceLocation(Category.DEFAULT_BACKGROUND);
        private ResourceLocation entryTextures = new ResourceLocation(Category.DEFAULT_ENTRY_TEXTURES);
        private BookConditionModel condition;
        private boolean showCategoryButton = true;

        private Builder() {
        }


        /**
         * Set the book this category belongs to.
         */
        public Builder withBook(BookModel book) {
            this.book = book;
            return this;
        }

        /**
         * Set the id of this category.
         * The ID must be unique within the book.
         *
         * @param id the cateogry ID, e.g. "modonomicon:features"
         */
        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        /**
         * Set the name of this category.
         *
         * @param name Should be a translation key.
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the category's icon.
         *
         * @param icon Either an item ResourceLocation (e.g.: "minecraft:stone") or a texture ResourceLocation (e.g. "minecraft:textures/block/stone.png" - note the ".png" at the end)
         */
        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the category's icon to the texture of the given item
         */
        public Builder withIcon(ItemLike item) {
            this.icon = ForgeRegistries.ITEMS.getKey(item.asItem()).toString();
            return this;
        }


        /**
         * Sets the category's sort number.
         * Categories with a lower sort number will be displayed first.
         */
        public Builder withSortNumber(int sortNumber) {
            this.sortNumber = sortNumber;
            return this;
        }

        /**
         * Sets the category's background texture.
         * The texture must be a 256x256 png file.
         * Default value is {@link Category#DEFAULT_BACKGROUND}
         */
        public Builder withBackground(ResourceLocation background) {
            this.background = background;
            return this;
        }

        /**
         * Sets the category's entry textures.
         * The texture must be a 256x256 png file.
         * This texture is used to display the entry background icons as well as the arrows connecting entries.
         * Default value is {@link Category#DEFAULT_ENTRY_TEXTURES}
         */
        public Builder withEntryTextures(ResourceLocation entryTextures) {
            this.entryTextures = entryTextures;
            return this;
        }

        /**
         * Replaces the current list of entries with the given list.
         */
        public Builder withEntries(List<BookEntryModel> entries) {
            this.entries.addAll(entries);
            return this;
        }

        /**
         * Adds the given entries to the list of entries.
         */
        public Builder withEntries(BookEntryModel... entries) {
            this.entries.addAll(List.of(entries));
            return this;
        }

        /**
         * Adds the given entry to the list of entries.
         */
        public Builder withEntry(BookEntryModel entry) {
            this.entries.add(entry);
            return this;
        }

        /**
         * Sets the condition that needs to be met for this category to be shown.
         * If no condition is set, the category will be unlocked by default.
         * Use {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel} or {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel} to combine multiple conditions.
         */
        public Builder withCondition(BookConditionModel condition) {
            this.condition = condition;
            return this;
        }

        /**
         * Sets whether the category button should be shown in the book.
         * Default value is true.
         * If false, the category will not have a navigation button, but can still be accessed by clicking on an entry or link that links to it.
         */
        public Builder withShowCategoryButton(boolean showCategoryButton) {
            this.showCategoryButton = showCategoryButton;
            return this;
        }

        public BookCategoryModel build() {
            BookCategoryModel bookCategoryModel = new BookCategoryModel();
            for (var entry : this.entries) {
                entry.category = bookCategoryModel;
                if (!entry.id.getPath().startsWith(this.id.getPath())) {
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
            bookCategoryModel.condition = this.condition;
            bookCategoryModel.showCategoryButton = this.showCategoryButton;

            return bookCategoryModel;
        }

        public BookModel getBook() {
            return this.book;
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

        public List<BookEntryModel> getEntries() {
            return this.entries;
        }

        public BookConditionModel getCondition() {
            return this.condition;
        }

        public boolean isShowCategoryButton() {
            return this.showCategoryButton;
        }
    }
}
