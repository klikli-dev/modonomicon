/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeLayout;

public class BookThemeLayoutModel {

    protected int bookTextOffsetX = 0;
    protected int bookTextOffsetY = 0;
    protected int bookTextOffsetWidth = 0;
    protected int bookTextOffsetHeight = 0;
    protected int categoryButtonXOffset = 0;
    protected int categoryButtonYOffset = 0;
    protected int searchButtonXOffset = 0;
    protected int searchButtonYOffset = 0;
    protected int readAllButtonYOffset = 0;
    protected float categoryButtonIconScale = 1.0f;

    public BookThemeLayout toData() {
        return new BookThemeLayout(this.bookTextOffsetX, this.bookTextOffsetY, this.bookTextOffsetWidth, this.bookTextOffsetHeight,
                this.categoryButtonXOffset, this.categoryButtonYOffset, this.searchButtonXOffset, this.searchButtonYOffset,
                this.readAllButtonYOffset, this.categoryButtonIconScale);
    }

    public BookThemeLayoutModel withBookTextOffsetX(int value) { this.bookTextOffsetX = value; return this; }
    public BookThemeLayoutModel withBookTextOffsetY(int value) { this.bookTextOffsetY = value; return this; }
    public BookThemeLayoutModel withBookTextOffsetWidth(int value) { this.bookTextOffsetWidth = value; return this; }
    public BookThemeLayoutModel withBookTextOffsetHeight(int value) { this.bookTextOffsetHeight = value; return this; }
    public BookThemeLayoutModel withCategoryButtonXOffset(int value) { this.categoryButtonXOffset = value; return this; }
    public BookThemeLayoutModel withCategoryButtonYOffset(int value) { this.categoryButtonYOffset = value; return this; }
    public BookThemeLayoutModel withSearchButtonXOffset(int value) { this.searchButtonXOffset = value; return this; }
    public BookThemeLayoutModel withSearchButtonYOffset(int value) { this.searchButtonYOffset = value; return this; }
    public BookThemeLayoutModel withReadAllButtonYOffset(int value) { this.readAllButtonYOffset = value; return this; }
    public BookThemeLayoutModel withCategoryButtonIconScale(float value) { this.categoryButtonIconScale = value; return this; }
}
