// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeNameStyle;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class BookNodeNameStyleModel {

    protected GuiSprite background = BookNodeNameStyle.DEFAULT.background();
    protected int backgroundTint = BookNodeNameStyle.DEFAULT.backgroundTint();
    protected int textColor = BookNodeNameStyle.DEFAULT.textColor();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("background", this.background.toJson());
        json.addProperty("background_tint", this.backgroundTint);
        json.addProperty("text_color", this.textColor);
        return json;
    }

    /**
     * Sets the default background sprite for permanently rendered entry names (see {@code render_name}).
     * Can be overridden per entry.
     */
    public BookNodeNameStyleModel withBackground(GuiSprite value) {
        this.background = value;
        return this;
    }

    /**
     * Sets the default ARGB tint for the entry name background.
     * Multiplied with the per-entry tint.
     */
    public BookNodeNameStyleModel withBackgroundTint(int value) {
        this.backgroundTint = value;
        return this;
    }

    /**
     * Sets the default ARGB color of the entry name text.
     * Multiplied with the per-entry text color.
     */
    public BookNodeNameStyleModel withTextColor(int value) {
        this.textColor = value;
        return this;
    }
}
