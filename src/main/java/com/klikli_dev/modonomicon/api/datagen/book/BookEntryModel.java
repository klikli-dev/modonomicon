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
import com.klikli_dev.modonomicon.book.BookCommand;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.registries.ForgeRegistries;

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
    protected ResourceLocation categoryToOpen;
    protected ResourceLocation commandToRunOnFirstRead;

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

        if (!this.parents.isEmpty()) {
            var parentsArray = new JsonArray();
            for (var parent : this.parents) {
                parentsArray.add(parent.toJson());
            }
            json.add("parents", parentsArray);
        }

        if (!this.pages.isEmpty()) {
            var pagesArray = new JsonArray();
            for (var page : this.pages) {
                pagesArray.add(page.toJson());
            }
            json.add("pages", pagesArray);
        }


        if (this.condition != null) {
            json.add("condition", this.condition.toJson());
        }

        if (this.categoryToOpen != null) {
            json.addProperty("category_to_open", this.categoryToOpen.toString());
        }
        if (this.commandToRunOnFirstRead != null) {
            json.addProperty("command_to_run_on_first_read", this.commandToRunOnFirstRead.toString());
        }

        return json;
    }

    public int getEntryBackgroundUIndex() {
        return this.entryBackgroundUIndex;
    }

    public int getEntryBackgroundVIndex() {
        return this.entryBackgroundVIndex;
    }

    public BookConditionModel getCondition() {
        return this.condition;
    }

    public ResourceLocation getCategoryToOpen() {
        return this.categoryToOpen;
    }

    public ResourceLocation getCommandToRunOnFirstRead() {
        return this.commandToRunOnFirstRead;
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
        public boolean hideWhileLocked;
        public List<BookPageModel> pages = new ArrayList<>();
        public BookConditionModel condition;
        private int entryBackgroundUIndex = 0;
        private int entryBackgroundVIndex = 0;
        private ResourceLocation categoryToOpen;
        private ResourceLocation commandToRunOnFirstRead;

        private Builder() {
        }

        /**
         * Sets the entry ID (= file name).
         * The ID must be unique within the book, so it is recommended to prepend the category: "<mod_id>:<cat_id>/<entry_id>".
         * @param id the entry ID, e.g. "modonomicon:features/image"
         */
        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the category this entry belongs to
         */
        public Builder withCategory(BookCategoryModel category) {
            this.category = category;
            return this;
        }

        /**
         * Replaces the Entry's parents with the given list.
         */
        public Builder withParents(List<BookEntryParentModel> parents) {
            this.parents = parents;
            return this;
        }

        /**
         * Adds the given parents to the Entry's parents.
         */
        public Builder withParents(BookEntryParentModel... parents) {
            this.parents.addAll(List.of(parents));
            return this;
        }

        /**
         * Adds the given parent to the Entry's parents.
         */
        public Builder withParent(BookEntryParentModel parent) {
            this.parents.add(parent);
            return this;
        }

        /**
         * Creates a default BookEntryParentModel from the given BookEntryModel and adds it to the Entry's parents.
         */
        public Builder withParent(BookEntryModel parent) {
            this.parents.add(BookEntryParentModel.builder().withEntryId(parent.id).build());
            return this;
        }


        /**
         * Creates a default BookEntryParentModel from the given BookEntryModel.Builder and adds it to the Entry's parents.
         */
        public Builder withParent(BookEntryModel.Builder parent) {
            this.parents.add(BookEntryParentModel.builder().withEntryId(parent.id).build());
            return this;
        }

        /**
         * Sets the entry's name.
         *
         * @param name Should be a translation key.
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the entry's description.
         *
         * @param description Should be a translation key.
         */
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the entry's icon.
         *
         * @param icon Either an item ResourceLocation (e.g.: "minecraft:stone") or a texture ResourceLocation (e.g. "minecraft:textures/block/stone.png" - note the ".png" at the end)
         */
        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the entry's icon to the texture of the given item
         */
        public Builder withIcon(ItemLike item) {
            this.icon = ForgeRegistries.ITEMS.getKey(item.asItem()).toString();
            return this;
        }

        /**
         * Sets the entry's position in the category screen.
         * Should usually be obtained from a {@link com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper}.
         */
        public Builder withLocation(Vec2 location) {
            return this.withX((int) location.x).withY((int) location.y);
        }

        /**
         * Sets the entry's position in the category screen.
         * Should usually be obtained from a {@link com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper}.
         */
        public Builder withLocation(int x, int y) {
            return this.withX(x).withY(y);
        }

        /**
         * Sets the entry's position in the category screen.
         * Should usually be obtained from a {@link com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper}.
         */
        public Builder withX(int x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the entry's position in the category screen.
         * Should usually be obtained from a {@link com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper}.
         */
        public Builder withY(int y) {
            this.y = y;
            return this;
        }

        /**
         * Select the entry background as found in the Category's "entry_textures" array.
         * You need to provide the starting UV coordinates of the background - use a tool like Photoshop or Photopea to find out the pixel coordinate of the upper left corner of the desired background.
         * U = Y Axis / Up-Down
         * V = X Axis / Left-Right
         */
        public Builder withEntryBackground(int u, int v) {
            this.entryBackgroundUIndex = u;
            this.entryBackgroundVIndex = v;
            return this;
        }

        /**
         * Select the entry background as found in the Category's "entry_textures" array.
         * You need to provide the starting UV coordinates of the background - use a tool like Photoshop or Photopea to find out the pixel coordinate of the upper left corner of the desired background.
         * First = U = Y Axis / Up-Down
         * Second = V = X Axis / Left-Right
         */
        public Builder withEntryBackground(Pair<Integer, Integer> uv) {
            this.entryBackgroundUIndex = uv.getFirst();
            this.entryBackgroundVIndex = uv.getSecond();
            return this;
        }

        /**
         * If true, the entry will not be shown while locked, even if the entry before it has been unlocked.
         * If false, the entry will be shown as greyed out once the entry before it has been unlocked.
         * If the entry before it is locked, it will not be shown independent of this setting.
         */
        public Builder hideWhileLocked(boolean hideWhileLocked) {
            this.hideWhileLocked = hideWhileLocked;
            return this;
        }

        /**
         * Replaces the entry's pages with the given list.
         */
        public Builder withPages(List<BookPageModel> pages) {
            this.pages = pages;
            return this;
        }

        /**
         * Adds the given pages to the entry's pages.
         */
        public Builder withPages(BookPageModel... pages) {
            this.pages.addAll(List.of(pages));
            return this;
        }

        /**
         * Adds the given page to the entry's pages.
         */
        public Builder withPage(BookPageModel page) {
            this.pages.add(page);
            return this;
        }

        /**
         * Sets the condition that needs to be met for this entry to be unlocked.
         * If no condition is set, the entry will be unlocked by default.
         * Use {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel} or {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel} to combine multiple conditions.
         */
        public Builder withCondition(BookConditionModel condition) {
            this.condition = condition;
            return this;
        }

        /**
         * If you provide a category resource location, this entry will not show book pages, but instead act as a link to that category.
         *
         * @param categoryToOpen The category to open when this entry is clicked. Should be a resource location (e.g.: "modonomicon:features").
         */
        public Builder withCategoryToOpen(ResourceLocation categoryToOpen) {
            this.categoryToOpen = categoryToOpen;
            return this;
        }

        /**
         * The command to run when this entry is first read.
         */
        public Builder withCommandToRunOnFirstRead(BookCommandModel bookCommandModel) {
            return this.withCommandToRunOnFirstRead(bookCommandModel.getId());
        }


        /**
         * The command to run when this entry is first read.
         */
        public Builder withCommandToRunOnFirstRead(ResourceLocation bookCommandModel) {
            this.commandToRunOnFirstRead = bookCommandModel;
            return this;
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

        public int getEntryBackgroundUIndex() {
            return this.entryBackgroundUIndex;
        }

        public int getEntryBackgroundVIndex() {
            return this.entryBackgroundVIndex;
        }

        public boolean isHideWhileLocked() {
            return this.hideWhileLocked;
        }

        public List<BookPageModel> getPages() {
            return this.pages;
        }

        public BookConditionModel getCondition() {
            return this.condition;
        }

        public ResourceLocation getCategoryToOpen() {
            return this.categoryToOpen;
        }

        public BookEntryModel build() {
            BookEntryModel bookEntryModel = new BookEntryModel();
            bookEntryModel.description = this.description;
            bookEntryModel.category = this.category;
            bookEntryModel.name = this.name;
            bookEntryModel.icon = this.icon;
            bookEntryModel.x = this.x;
            bookEntryModel.y = this.y;
            bookEntryModel.entryBackgroundUIndex = this.entryBackgroundUIndex;
            bookEntryModel.entryBackgroundVIndex = this.entryBackgroundVIndex;
            bookEntryModel.hideWhileLocked = this.hideWhileLocked;
            bookEntryModel.parents = this.parents;
            bookEntryModel.id = this.id;
            bookEntryModel.pages = this.pages;
            bookEntryModel.condition = this.condition;
            bookEntryModel.categoryToOpen = this.categoryToOpen;
            bookEntryModel.commandToRunOnFirstRead = this.commandToRunOnFirstRead;
            return bookEntryModel;
        }
    }
}
