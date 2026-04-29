/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class BookEntryModel {
    protected Identifier id;
    protected BookCategoryModel category;
    protected List<BookEntryParentModel> parents = new ArrayList<>();
    protected String name;
    protected String description = "";
    protected BookIconModel icon;
    protected int x;
    protected int y;
    protected GuiSprite entryBackground = GuiSprite.EMPTY;

    protected boolean hideWhileLocked;
    protected boolean showWhenAnyParentUnlocked;
    protected List<BookPageModel<?>> pages = new ArrayList<>();
    protected BookConditionModel<?> condition;
    protected Identifier categoryToOpen;
    protected Identifier commandToRunOnFirstRead;
    protected Identifier entryToOpen;

    protected int sortNumber = -1;

    protected BookEntryModel(Identifier id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id   The entry ID, e.g. "modonomicon:features/test". The ID must be unique within the book.
     * @param name Should be a translation key.
     */
    public static BookEntryModel create(Identifier id, String name) {
        return new BookEntryModel(id, name);
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();

        //if we are in the same namespace, which we basically always should be, omit namespace
        if (this.category.getId().getNamespace().equals(this.getId().getNamespace()))
            json.addProperty("category", this.category.id.getPath());
        else
            json.addProperty("category", this.category.getId().toString());

        json.addProperty("name", this.name);
        json.addProperty("description", this.description);
        json.add("icon", this.icon.toJson(provider));
        json.addProperty("x", this.x);
        json.addProperty("y", this.y);
        json.add("background", this.entryBackground.toJson());
        json.addProperty("hide_while_locked", this.hideWhileLocked);
        json.addProperty("show_when_any_parent_unlocked", this.showWhenAnyParentUnlocked);

        if (!this.parents.isEmpty()) {
            var parentsArray = new JsonArray();
            for (var parent : this.parents) {
                parentsArray.add(parent.toJson(this.getId(), provider));
            }
            json.add("parents", parentsArray);
        }

        if (!this.pages.isEmpty()) {
            var pagesArray = new JsonArray();
            for (var page : this.pages) {
                pagesArray.add(page.toJson(this.getId(), provider));
            }
            json.add("pages", pagesArray);
        }

        if (this.condition != null) {
            json.add("condition", this.condition.toJson(this.getId(), provider));
        }

        if (this.categoryToOpen != null) {
            //if we are in the same namespace, which we basically always should be, omit namespace
            if (this.categoryToOpen.getNamespace().equals(this.getId().getNamespace()))
                json.addProperty("category_to_open", this.categoryToOpen.getPath());
            else
                json.addProperty("category_to_open", this.categoryToOpen.toString());
        }
        if (this.commandToRunOnFirstRead != null) {
            //if we are in the same namespace, which we basically always should be, omit namespace
            if (this.commandToRunOnFirstRead.getNamespace().equals(this.getId().getNamespace()))
                json.addProperty("command_to_run_on_first_read", this.commandToRunOnFirstRead.getPath());
            else
                json.addProperty("command_to_run_on_first_read", this.commandToRunOnFirstRead.toString());
        }
        if (this.entryToOpen != null) {
            //if we are in the same namespace, which we basically always should be, omit namespace
            if (this.entryToOpen.getNamespace().equals(this.getId().getNamespace()))
                json.addProperty("entry_to_open", this.entryToOpen.getPath());
            else
                json.addProperty("entry_to_open", this.entryToOpen.toString());
        }

        json.addProperty("sort_number", this.sortNumber);

        return json;
    }

    public GuiSprite getEntryBackground() {
        return this.entryBackground;
    }

    public BookConditionModel<?> getCondition() {
        return this.condition;
    }

    public Identifier getCategoryToOpen() {
        return this.categoryToOpen;
    }

    public Identifier getCommandToRunOnFirstRead() {
        return this.commandToRunOnFirstRead;
    }

    public Identifier getEntryToOpen() {
        return this.entryToOpen;
    }

    public Identifier getId() {
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

    public BookIconModel getIcon() {
        return this.icon;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean hideWhileLocked() {
        return this.hideWhileLocked;
    }

    public boolean showWhenAnyParentUnlocked() {
        return this.showWhenAnyParentUnlocked;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public List<BookPageModel<?>> getPages() {
        return this.pages;
    }

    /**
     * Sets the entry ID (= file name).
     * The ID must be unique within the book, so it is recommended to prepend the category: "<mod_id>:<cat_id>/<entry_id>".
     *
     * @param id the entry ID, e.g. "modonomicon:features/image"
     */
    public BookEntryModel withId(Identifier id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the category this entry belongs to
     */
    public BookEntryModel withCategory(BookCategoryModel category) {
        this.category = category;
        return this;
    }

    /**
     * Replaces the Entry's parents with the given list.
     */
    public BookEntryModel withParents(List<BookEntryParentModel> parents) {
        this.parents = parents;
        return this;
    }

    /**
     * Adds the given parents to the Entry's parents.
     */
    public BookEntryModel withParents(BookEntryParentModel... parents) {
        this.parents.addAll(List.of(parents));
        return this;
    }

    /**
     * Adds the given parent to the Entry's parents.
     */
    public BookEntryModel withParent(BookEntryParentModel parent) {
        this.parents.add(parent);
        return this;
    }

    /**
     * Creates a default BookEntryParentModel from the given BookEntryModel and adds it to the Entry's parents.
     */
    public BookEntryModel withParent(BookEntryModel parent) {
        this.parents.add(BookEntryParentModel.create(parent.id));
        return this;
    }

    /**
     * Sets the entry's name.
     *
     * @param name Should be a translation key.
     */
    public BookEntryModel withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the entry's description.
     *
     * @param description Should be a translation key.
     */
    public BookEntryModel withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the entry's icon.
     */
    public BookEntryModel withIcon(BookIconModel icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the entry's icon as the given texture resource location
     */
    public BookEntryModel withIcon(Identifier texture) {
        this.icon = BookIconModel.create(texture);
        return this;
    }

    /**
     * Sets the entry's icon as the given texture resource location with the given size
     */
    public BookEntryModel withIcon(Identifier texture, int width, int height) {
        this.icon = BookIconModel.create(texture, width, height);
        return this;
    }

    /**
     * Sets the entry's icon to the texture of the given item
     */
    public BookEntryModel withIcon(ItemLike item) {
        this.icon = BookIconModel.create(item);
        return this;
    }

    /**
     * Sets the entry's position in the category screen.
     * Should usually be obtained from a {@link CategoryEntryMap}.
     */
    public BookEntryModel withLocation(Vec2 location) {
        return this.withX((int) location.x).withY((int) location.y);
    }

    /**
     * Sets the entry's position in the category screen.
     * Should usually be obtained from a {@link CategoryEntryMap}.
     */
    public BookEntryModel withLocation(int x, int y) {
        return this.withX(x).withY(y);
    }

    /**
     * Sets the entry's position in the category screen.
     * Should usually be obtained from a {@link CategoryEntryMap}.
     */
    public BookEntryModel withX(int x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the entry's position in the category screen.
     * Should usually be obtained from a {@link CategoryEntryMap}.
     */
    public BookEntryModel withY(int y) {
        this.y = y;
        return this;
    }

    /**
     * Select the themed node entry background by GUI sprite id and optional dimensions.
     */
    public BookEntryModel withEntryBackground(GuiSprite texture) {
        this.entryBackground = texture;
        return this;
    }

    /**
     * If true, the entry will not be shown while locked, even if the entry before it has been unlocked.
     * If false, the entry will be shown as greyed out once the entry before it has been unlocked.
     * If the entry before it is locked, it will not be shown independent of this setting.
     */
    public BookEntryModel hideWhileLocked(boolean hideWhileLocked) {
        this.hideWhileLocked = hideWhileLocked;
        return this;
    }

    /**
     * If true, the entry will show (locked) as soon as any parent is unlocked.
     * If false, the entry will only show (locked) as soon as all parents are unlocked.
     */
    public BookEntryModel showWhenAnyParentUnlocked(boolean showWhenAnyParentUnlocked) {
        this.showWhenAnyParentUnlocked = showWhenAnyParentUnlocked;
        return this;
    }

    /**
     * Sets the entry's sort number. Only used if the parent category is in index mode.
     * Entries with a lower sort number will be displayed first in the index list.
     * <p>
     * If no sort number is provided the CategoryProvider will add sort numbers in the order the entries are added.
     */
    public BookEntryModel withSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
        return this;
    }

    /**
     * Replaces the entry's pages with the given list.
     */
    public BookEntryModel withPages(List<BookPageModel<?>> pages) {
        this.pages = pages;
        return this;
    }

    /**
     * Adds the given pages to the entry's pages.
     */
    public BookEntryModel withPages(BookPageModel<?>... pages) {
        this.pages.addAll(List.of(pages));
        return this;
    }

    /**
     * Adds the given page to the entry's pages.
     */
    public BookEntryModel withPage(BookPageModel<?> page) {
        this.pages.add(page);
        return this;
    }

    /**
     * Sets the condition that needs to be met for this entry to be unlocked.
     * If no condition is set, the entry will be unlocked by default.
     * Use {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel} or {@link com.klikli_dev.modonomicon.api.datagen.book.condition.BookOrConditionModel} to combine multiple conditions.
     */
    public BookEntryModel withCondition(BookConditionModel<?> condition) {
        this.condition = condition;
        return this;
    }

    /**
     * If you provide a category resource location, this entry will not show book pages, but instead act as a link to that category.
     *
     * @param categoryToOpen The category to open when this entry is clicked. Should be a resource location (e.g.: "modonomicon:features").
     */
    public BookEntryModel withCategoryToOpen(Identifier categoryToOpen) {
        this.categoryToOpen = categoryToOpen;
        return this;
    }

    /**
     * The command to run when this entry is first read.
     */
    public BookEntryModel withCommandToRunOnFirstRead(BookCommandModel bookCommandModel) {
        return this.withCommandToRunOnFirstRead(bookCommandModel.getId());
    }

    /**
     * The command to run when this entry is first read.
     */
    public BookEntryModel withCommandToRunOnFirstRead(Identifier bookCommandModel) {
        this.commandToRunOnFirstRead = bookCommandModel;
        return this;
    }

    /**
     * If you provide an entry resource location, this entry will not show book pages, but instead act as a link to that entry.
     *
     * @param entryToOpen The entry to open when this entry is clicked. Should be a resource location (e.g.: "modonomicon:features/image").
     */
    public BookEntryModel withEntryToOpen(Identifier entryToOpen) {
        this.entryToOpen = entryToOpen;
        return this;
    }
}

