/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.client.gui.book.theme.defaults.DefaultBookTheme;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class BookThemeModel {

    protected Identifier id = DefaultBookTheme.ID;
    protected Identifier type = DefaultBookTheme.ID;
    protected BookThemeLayoutModel layout = new BookThemeLayoutModel();
    protected BookThemeContentModel content = new BookThemeContentModel();
    protected BookThemePaletteModel palette = new BookThemePaletteModel();
    protected BookNodeSettingsModel node = new BookNodeSettingsModel();
    protected boolean generateJson;

    /**
     * Serializes this theme model to the generated JSON form.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id.toString());
        json.addProperty("type", this.type.toString());
        json.add("layout", this.layout.toJson());
        if (!this.content.isEmpty()) {
            json.add("content", this.content.toJson());
        }
        json.add("palette", this.palette.toJson());
        json.add("node", this.node.toJson());
        return json;
    }

    /**
     * Returns whether this model should emit a generated theme JSON file.
     */
    public boolean shouldGenerateJson() {
        return this.generateJson;
    }

    /**
     * Sets the generated theme id.
     */
    public BookThemeModel withId(Identifier id) {
        this.id = id;
        this.generateJson = true;
        return this;
    }

    /**
     * Sets the registered theme type used to construct the runtime theme.
     */
    public BookThemeModel withType(Identifier type) {
        this.type = type;
        this.generateJson = true;
        return this;
    }

    /**
     * Configures theme layout offsets and scaling.
     */
    public BookThemeModel withLayout(Consumer<BookThemeLayoutModel> consumer) {
        consumer.accept(this.layout);
        this.generateJson = true;
        return this;
    }

    /**
     * Configures theme content sprites.
     */
    public BookThemeModel withContent(Consumer<BookThemeContentModel> consumer) {
        consumer.accept(this.content);
        this.generateJson = true;
        return this;
    }

    /**
     * Configures theme palette colors.
     */
    public BookThemeModel withPalette(Consumer<BookThemePaletteModel> consumer) {
        consumer.accept(this.palette);
        this.generateJson = true;
        return this;
    }

    /**
     * Configures node-screen connection rendering settings.
     */
    public BookThemeModel withNode(Consumer<BookNodeSettingsModel> consumer) {
        consumer.accept(this.node);
        this.generateJson = true;
        return this;
    }
}
