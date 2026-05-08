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

/**
 * Data-driven definition of a book theme.
 *
 * @param id unique identifier of the theme instance.
 * @param type registered theme factory id used to construct the runtime theme.
 * @param layout layout offsets and scaling used across the book UI.
 * @param palette default title/text colors used by theme-aware content rendering.
 * @param node settings for node-screen connection rendering, including renderer selection and direct renderer configuration.
 */
public record BookThemeData(Identifier id, Identifier type, BookThemeLayout layout, BookThemePalette palette, BookNodeSettings node) {

    public static BookThemeData defaults() {
        return new BookThemeData(
                ModonomiconConstants.Data.Theme.DEFAULT_THEME_ID,
                ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE,
                BookThemeLayout.DEFAULT,
                BookThemePalette.DEFAULT,
                BookNodeSettings.DEFAULT
        );
    }

    public static BookThemeData fromJson(JsonObject json) {
        var defaults = defaults();
        return new BookThemeData(
                Identifier.parse(GsonHelper.getAsString(json, "id", defaults.id.toString())),
                Identifier.parse(GsonHelper.getAsString(json, "type", defaults.type.toString())),
                json.has("layout") ? BookThemeLayout.fromJson(GsonHelper.getAsJsonObject(json, "layout")) : defaults.layout,
                json.has("palette") ? BookThemePalette.fromJson(GsonHelper.getAsJsonObject(json, "palette")) : defaults.palette,
                json.has("node") ? BookNodeSettings.fromJson(GsonHelper.getAsJsonObject(json, "node")) : defaults.node
        );
    }

    public static BookThemeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookThemeData(
                buffer.readIdentifier(),
                buffer.readIdentifier(),
                BookThemeLayout.fromNetwork(buffer),
                BookThemePalette.fromNetwork(buffer),
                BookNodeSettings.fromNetwork(buffer)
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeIdentifier(this.id);
        buffer.writeIdentifier(this.type);
        this.layout.toNetwork(buffer);
        this.palette.toNetwork(buffer);
        this.node.toNetwork(buffer);
    }
}
