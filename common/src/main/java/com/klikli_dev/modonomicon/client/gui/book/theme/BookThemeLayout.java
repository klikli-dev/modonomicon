/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public record BookThemeLayout(int bookTextOffsetX, int bookTextOffsetY, int bookTextOffsetWidth, int bookTextOffsetHeight,
                              int categoryButtonXOffset, int categoryButtonYOffset,
                              int searchButtonXOffset, int searchButtonYOffset,
                              int readAllButtonYOffset, float categoryButtonIconScale) implements BookLayoutTheme {

    public static final BookThemeLayout DEFAULT = new BookThemeLayout(0, 0, 0, 0, 0, 0, 0, 0, 0, 1.0f);

    public static BookThemeLayout fromJson(JsonObject json) {
        return new BookThemeLayout(
                GsonHelper.getAsInt(json, "book_text_offset_x", DEFAULT.bookTextOffsetX),
                GsonHelper.getAsInt(json, "book_text_offset_y", DEFAULT.bookTextOffsetY),
                GsonHelper.getAsInt(json, "book_text_offset_width", DEFAULT.bookTextOffsetWidth),
                GsonHelper.getAsInt(json, "book_text_offset_height", DEFAULT.bookTextOffsetHeight),
                GsonHelper.getAsInt(json, "category_button_x_offset", DEFAULT.categoryButtonXOffset),
                GsonHelper.getAsInt(json, "category_button_y_offset", DEFAULT.categoryButtonYOffset),
                GsonHelper.getAsInt(json, "search_button_x_offset", DEFAULT.searchButtonXOffset),
                GsonHelper.getAsInt(json, "search_button_y_offset", DEFAULT.searchButtonYOffset),
                GsonHelper.getAsInt(json, "read_all_button_y_offset", DEFAULT.readAllButtonYOffset),
                GsonHelper.getAsFloat(json, "category_button_icon_scale", DEFAULT.categoryButtonIconScale)
        );
    }

    public static BookThemeLayout fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookThemeLayout(
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readFloat()
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeShort(this.bookTextOffsetX);
        buffer.writeShort(this.bookTextOffsetY);
        buffer.writeShort(this.bookTextOffsetWidth);
        buffer.writeShort(this.bookTextOffsetHeight);
        buffer.writeShort(this.categoryButtonXOffset);
        buffer.writeShort(this.categoryButtonYOffset);
        buffer.writeShort(this.searchButtonXOffset);
        buffer.writeShort(this.searchButtonYOffset);
        buffer.writeShort(this.readAllButtonYOffset);
        buffer.writeFloat(this.categoryButtonIconScale);
    }
}
