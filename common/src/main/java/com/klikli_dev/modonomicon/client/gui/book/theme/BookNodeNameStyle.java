// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * Theme-level defaults for permanently rendered entry names (see {@code render_name}).
 * Each value can be overridden per entry.
 *
 * @param background default background sprite. Tint-less by default so tinting works properly.
 * @param backgroundTint default ARGB tint, multiplied with the per-entry tint.
 * @param textColor default ARGB text color, multiplied with the per-entry text color.
 */
public record BookNodeNameStyle(GuiSprite background, int backgroundTint, int textColor) {

    public static final BookNodeNameStyle DEFAULT = new BookNodeNameStyle(
            new GuiSprite(Modonomicon.loc("modonomicon/themes/default/node/entry_backgrounds/name_background"), 200, 26),
            0xFFFFFFFF,
            0xFFFFFFFF
    );

    public static BookNodeNameStyle fromJson(JsonObject json) {
        return new BookNodeNameStyle(
                json.has("background") ? GuiSprite.fromJson(json.get("background")) : DEFAULT.background,
                GsonHelper.getAsInt(json, "background_tint", DEFAULT.backgroundTint),
                GsonHelper.getAsInt(json, "text_color", DEFAULT.textColor)
        );
    }

    public static BookNodeNameStyle fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookNodeNameStyle(
                GuiSprite.STREAM_CODEC.decode(buffer),
                buffer.readInt(),
                buffer.readInt()
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        GuiSprite.STREAM_CODEC.encode(buffer, this.background);
        buffer.writeInt(this.backgroundTint);
        buffer.writeInt(this.textColor);
    }
}
