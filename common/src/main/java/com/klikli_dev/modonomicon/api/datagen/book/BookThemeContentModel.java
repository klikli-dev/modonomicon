/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class BookThemeContentModel {

    @Nullable
    protected GuiSprite defaultCategoryButtonSprite = null;

    public boolean isEmpty() {
        return this.defaultCategoryButtonSprite == null;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (this.defaultCategoryButtonSprite != null) {
            json.add("default_category_button_sprite", this.defaultCategoryButtonSprite.toJson());
        }
        return json;
    }

    /**
     * Sets the default sprite used for category buttons.
     */
    public BookThemeContentModel withDefaultCategoryButtonSprite(@Nullable GuiSprite sprite) {
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
