/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public record BookThemeData(Identifier id, Identifier type, BookThemeLayout layout, BookThemePalette palette) {

    public static BookThemeData defaults() {
        return new BookThemeData(
                ModonomiconConstants.Data.Theme.DEFAULT_THEME_ID,
                ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE,
                BookThemeLayout.DEFAULT,
                BookThemePalette.DEFAULT
        );
    }

    public static BookThemeData fromJson(JsonObject json) {
        var defaults = defaults();
        return new BookThemeData(
                Identifier.parse(GsonHelper.getAsString(json, "id", defaults.id.toString())),
                Identifier.parse(GsonHelper.getAsString(json, "type", defaults.type.toString())),
                json.has("layout") ? BookThemeLayout.fromJson(GsonHelper.getAsJsonObject(json, "layout")) : defaults.layout,
                json.has("palette") ? BookThemePalette.fromJson(GsonHelper.getAsJsonObject(json, "palette")) : defaults.palette
        );
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id.toString());
        json.addProperty("type", this.type.toString());
        json.add("layout", this.layout.toJson());
        json.add("palette", this.palette.toJson());
        return json;
    }

    public static BookThemeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookThemeData(
                buffer.readIdentifier(),
                buffer.readIdentifier(),
                BookThemeLayout.fromNetwork(buffer),
                BookThemePalette.fromNetwork(buffer)
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeIdentifier(this.id);
        buffer.writeIdentifier(this.type);
        this.layout.toNetwork(buffer);
        this.palette.toNetwork(buffer);
    }
}
