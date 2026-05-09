/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;

public class BookThemeLayoutModel {

    protected int bookTextOffsetX = 0;
    protected int bookTextOffsetY = 0;
    protected int bookTextOffsetWidth = 0;
    protected int bookTextOffsetHeight = 0;
    protected int categoryButtonXOffset = 0;
    protected int categoryButtonYOffset = 2;
    protected int searchButtonXOffset = 0;
    protected int searchButtonYOffset = 0;
    protected int readAllButtonYOffset = 0;
    protected float categoryButtonIconScale = 1.0f;

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("book_text_offset_x", this.bookTextOffsetX);
        json.addProperty("book_text_offset_y", this.bookTextOffsetY);
        json.addProperty("book_text_offset_width", this.bookTextOffsetWidth);
        json.addProperty("book_text_offset_height", this.bookTextOffsetHeight);
        json.addProperty("category_button_x_offset", this.categoryButtonXOffset);
        json.addProperty("category_button_y_offset", this.categoryButtonYOffset);
        json.addProperty("search_button_x_offset", this.searchButtonXOffset);
        json.addProperty("search_button_y_offset", this.searchButtonYOffset);
        json.addProperty("read_all_button_y_offset", this.readAllButtonYOffset);
        json.addProperty("category_button_icon_scale", this.categoryButtonIconScale);
        return json;
    }

    /**
     * Sets the horizontal offset applied to page text.
     */
    public BookThemeLayoutModel withBookTextOffsetX(int value) {
        this.bookTextOffsetX = value;
        return this;
    }

    /**
     * Sets the vertical offset applied to page text.
     */
    public BookThemeLayoutModel withBookTextOffsetY(int value) {
        this.bookTextOffsetY = value;
        return this;
    }

    /**
     * Adjusts the available width for page text.
     */
    public BookThemeLayoutModel withBookTextOffsetWidth(int value) {
        this.bookTextOffsetWidth = value;
        return this;
    }

    /**
     * Adjusts the available height for page text.
     */
    public BookThemeLayoutModel withBookTextOffsetHeight(int value) {
        this.bookTextOffsetHeight = value;
        return this;
    }

    /**
     * Sets the horizontal offset of category buttons in node mode.
     */
    public BookThemeLayoutModel withCategoryButtonXOffset(int value) {
        this.categoryButtonXOffset = value;
        return this;
    }

    /**
     * Sets the vertical offset of category buttons in node mode.
     */
    public BookThemeLayoutModel withCategoryButtonYOffset(int value) {
        this.categoryButtonYOffset = value;
        return this;
    }

    /**
     * Sets the horizontal offset of the search button.
     */
    public BookThemeLayoutModel withSearchButtonXOffset(int value) {
        this.searchButtonXOffset = value;
        return this;
    }

    /**
     * Sets the vertical offset of the search button.
     */
    public BookThemeLayoutModel withSearchButtonYOffset(int value) {
        this.searchButtonYOffset = value;
        return this;
    }

    /**
     * Sets the vertical offset of the read-all button.
     */
    public BookThemeLayoutModel withReadAllButtonYOffset(int value) {
        this.readAllButtonYOffset = value;
        return this;
    }

    /**
     * Sets the icon scale used by category buttons.
     */
    public BookThemeLayoutModel withCategoryButtonIconScale(float value) {
        this.categoryButtonIconScale = value;
        return this;
    }
}
