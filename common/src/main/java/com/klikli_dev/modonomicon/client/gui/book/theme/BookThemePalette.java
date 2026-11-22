/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public record BookThemePalette(int defaultTitleColor, int defaultTextColor, int tabOverlayDimColor) implements BookPaletteTheme {

    public static final int DEFAULT_TAB_OVERLAY_DIM_COLOR = 0x60000000;

    public static final BookThemePalette DEFAULT = new BookThemePalette(
            ModonomiconConstants.Data.Book.DEFAULT_TITLE_COLOR,
            ModonomiconConstants.Data.Book.DEFAULT_TEXT_COLOR,
            DEFAULT_TAB_OVERLAY_DIM_COLOR
    );

    public static BookThemePalette fromJson(JsonObject json) {
        return new BookThemePalette(
                GsonHelper.getAsInt(json, "default_title_color", DEFAULT.defaultTitleColor),
                GsonHelper.getAsInt(json, "default_text_color", DEFAULT.defaultTextColor),
                GsonHelper.getAsInt(json, "tab_overlay_dim_color", DEFAULT.tabOverlayDimColor)
        );
    }

    public static BookThemePalette fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookThemePalette(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.defaultTitleColor);
        buffer.writeInt(this.defaultTextColor);
        buffer.writeInt(this.tabOverlayDimColor);
    }
}
