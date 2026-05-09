/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class BookThemeContentModel {

    protected GuiSprite defaultCategoryButtonSprite = GuiSprite.EMPTY;

    public boolean isEmpty() {
        return this.defaultCategoryButtonSprite.isEmpty();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!this.defaultCategoryButtonSprite.isEmpty()) {
            json.add("default_category_button_sprite", this.defaultCategoryButtonSprite.toJson());
        }
        return json;
    }

    /**
     * Sets the default sprite used for category buttons.
     */
    public BookThemeContentModel withDefaultCategoryButtonSprite(GuiSprite sprite) {
        this.defaultCategoryButtonSprite = sprite;
        return this;
    }

    /**
     * Sets the default sprite used for category buttons.
     */
    public BookThemeContentModel withDefaultCategoryButtonSprite(Identifier sprite, int width, int height) {
        return this.withDefaultCategoryButtonSprite(new GuiSprite(sprite, width, height));
    }
}
