/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record BookThemeContentData(GuiSprite defaultCategoryButtonSprite) {

    public static final BookThemeContentData DEFAULT = new BookThemeContentData(GuiSprite.EMPTY);

    public static BookThemeContentData fromJson(JsonObject json) {
        return new BookThemeContentData(
                json.has("default_category_button_sprite")
                        ? GuiSprite.fromJson(json.get("default_category_button_sprite"))
                        : GuiSprite.EMPTY
        );
    }

    public static BookThemeContentData fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookThemeContentData(readOptionalSprite(buffer));
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        writeOptionalSprite(buffer, this.defaultCategoryButtonSprite);
    }

    private static GuiSprite readOptionalSprite(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? GuiSprite.fromNetwork(buffer) : GuiSprite.EMPTY;
    }

    private static void writeOptionalSprite(RegistryFriendlyByteBuf buffer, GuiSprite sprite) {
        buffer.writeBoolean(!sprite.isEmpty());
        if (!sprite.isEmpty()) {
            sprite.toNetwork(buffer);
        }
    }
}
