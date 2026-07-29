/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemePalette;

public class BookThemePaletteModel {

    protected int defaultTitleColor = BookThemePalette.DEFAULT.defaultTitleColor();
    protected int defaultTextColor = BookThemePalette.DEFAULT.defaultTextColor();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("default_title_color", this.defaultTitleColor);
        json.addProperty("default_text_color", this.defaultTextColor);
        return json;
    }

    /**
     * Sets the default title color used by theme-aware content.
     */
    public BookThemePaletteModel withDefaultTitleColor(int value) {
        this.defaultTitleColor = value;
        return this;
    }

    /**
     * Sets the default body text color used by theme-aware content.
     */
    public BookThemePaletteModel withDefaultTextColor(int value) {
        this.defaultTextColor = value;
        return this;
    }
}
