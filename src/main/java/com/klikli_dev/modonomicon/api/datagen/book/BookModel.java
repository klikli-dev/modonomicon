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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookModel {

    protected ResourceLocation id;
    protected String name;
    protected String tooltip = "";
    protected String creativeTab = "modonomicon";

    protected ResourceLocation model = new ResourceLocation(Book.DEFAULT_MODEL);
    protected ResourceLocation bookOverviewTexture = new ResourceLocation(Data.Book.DEFAULT_OVERVIEW_TEXTURE);

    protected ResourceLocation bookContentTexture = new ResourceLocation(Data.Book.DEFAULT_CONTENT_TEXTURE);

    protected ResourceLocation craftingTexture = new ResourceLocation(Book.DEFAULT_CRAFTING_TEXTURE);
    protected int defaultTitleColor = 0x00000;

    protected List<BookCategoryModel> categories = new ArrayList<>();

    protected boolean autoAddReadConditions = false;
    protected boolean generateBookItem = true;

    @Nullable
    protected ResourceLocation customBookItem = null;

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX = 0;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY = 0;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth = 0;

    protected BookModel(ResourceLocation id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id The book ID, e.g. "modonomicon:demo". The ID must be unique (usually that is guaranteed by the mod ID).
     * @param name Should be a translation key.
     */
    public static BookModel create(ResourceLocation id, String name) {
        return new BookModel(id, name);
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    @Nullable
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

    public BookModel withTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public BookModel withCreativeTab(String creativeTab) {
        this.creativeTab = creativeTab;
        return this;
    }

    public BookModel withBookOverviewTexture(ResourceLocation bookOverviewTexture) {
        this.bookOverviewTexture = bookOverviewTexture;
        return this;
    }

    public BookModel withBookContentTexture(ResourceLocation bookContentTexture) {
        this.bookContentTexture = bookContentTexture;
        return this;
    }

    public BookModel withCraftingTexture(ResourceLocation craftingTexture) {
        this.craftingTexture = craftingTexture;
        return this;
    }

    public BookModel withModel(ResourceLocation model) {
        this.model = model;
        return this;
    }

    public BookModel withGenerateBookItem(boolean generateBookItem) {
        this.generateBookItem = generateBookItem;
        return this;
    }

    public BookModel withCustomBookItem(ResourceLocation customBookItem) {
        this.customBookItem = customBookItem;
        return this;
    }

    public BookModel withDefaultTitleColor(int defaultTitleColor) {
        this.defaultTitleColor = defaultTitleColor;
        return this;
    }

    public BookModel withCategories(List<BookCategoryModel> categories) {
        categories.forEach(category -> category.book = this);
        this.categories.addAll(categories);
        return this;
    }

    public BookModel withCategories(BookCategoryModel... categories) {
        return this.withCategories(List.of(categories));
    }

    public BookModel withCategory(BookCategoryModel category) {
        category.book = this;
        this.categories.add(category);
        return this;
    }

    public BookModel withAutoAddReadConditions(boolean autoAddReadConditions) {
        this.autoAddReadConditions = autoAddReadConditions;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    public BookModel withBookTextOffsetX(int bookTextOffsetX) {
        this.bookTextOffsetX = bookTextOffsetX;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    public BookModel withBookTextOffsetY(int bookTextOffsetY) {
        this.bookTextOffsetY = bookTextOffsetY;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    public BookModel withBookTextOffsetWidth(int bookTextOffsetWidth) {
        this.bookTextOffsetWidth = bookTextOffsetWidth;
        return this;
    }
}
