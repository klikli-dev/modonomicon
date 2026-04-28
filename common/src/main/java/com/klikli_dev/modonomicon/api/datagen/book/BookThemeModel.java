/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class BookThemeModel {

    protected Identifier id = ModonomiconConstants.Data.Theme.DEFAULT_THEME_ID;
    protected Identifier type = ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE;
    protected BookThemeLayoutModel layout = new BookThemeLayoutModel();
    protected BookThemePaletteModel palette = new BookThemePaletteModel();
    protected boolean generateJson;

    public BookThemeData toData() {
        return new BookThemeData(this.id, this.type, this.layout.toData(), this.palette.toData());
    }

    public JsonObject toJson() {
        return this.toData().toJson();
    }

    public boolean shouldGenerateJson() {
        return this.generateJson;
    }

    public BookThemeModel withId(Identifier id) {
        this.id = id;
        this.generateJson = true;
        return this;
    }

    public BookThemeModel withType(Identifier type) {
        this.type = type;
        this.generateJson = true;
        return this;
    }

    public BookThemeModel withLayout(Consumer<BookThemeLayoutModel> consumer) {
        consumer.accept(this.layout);
        this.generateJson = true;
        return this;
    }

    public BookThemeModel withPalette(Consumer<BookThemePaletteModel> consumer) {
        consumer.accept(this.palette);
        this.generateJson = true;
        return this;
    }
}
