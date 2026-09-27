/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * Theme-level settings controlling how node connections are rendered.
 *
 * @param connectionRenderer selects whether node connections use the existing routed sprite renderer or the direct line renderer.
 * @param directConnections configures the direct line renderer when {@link #connectionRenderer()} is set to {@link NodeConnectionRendererType#DIRECT}.
 * @param entryNameBackground default background sprite for permanently rendered entry names (see {@code render_name}).
 * Can be overridden per entry via {@code name_background}. Tint-less by default so tinting works properly.
 * @param entryNameBackgroundTint default ARGB tint for the entry name background, multiplied with the per-entry tint.
 * @param entryNameTextColor default ARGB color of the entry name text, multiplied with the per-entry text color.
 */
public record BookNodeSettings(NodeConnectionRendererType connectionRenderer, BookDirectConnectionTheme directConnections,
                               GuiSprite entryNameBackground, int entryNameBackgroundTint, int entryNameTextColor) {
    public static final BookNodeSettings DEFAULT = new BookNodeSettings(
            NodeConnectionRendererType.SPRITE,
            BookDirectConnectionTheme.DEFAULT,
            new GuiSprite(Modonomicon.loc("modonomicon/themes/default/node/entry_backgrounds/name_background"), 200, 26),
            0xFFFFFFFF,
            0xFFFFFFFF
    );

    public static BookNodeSettings fromJson(JsonObject json) {
        return new BookNodeSettings(
                NodeConnectionRendererType.fromString(GsonHelper.getAsString(json, "connection_renderer", DEFAULT.connectionRenderer.serializedName())),
                json.has("direct_connections") ? BookDirectConnectionTheme.fromJson(GsonHelper.getAsJsonObject(json, "direct_connections")) : DEFAULT.directConnections,
                json.has("entry_name_background") ? GuiSprite.fromJson(json.get("entry_name_background")) : DEFAULT.entryNameBackground,
                GsonHelper.getAsInt(json, "entry_name_background_tint", DEFAULT.entryNameBackgroundTint),
                GsonHelper.getAsInt(json, "entry_name_text_color", DEFAULT.entryNameTextColor)
        );
    }

    public static BookNodeSettings fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookNodeSettings(
                buffer.readEnum(NodeConnectionRendererType.class),
                BookDirectConnectionTheme.fromNetwork(buffer),
                GuiSprite.STREAM_CODEC.decode(buffer),
                buffer.readInt(),
                buffer.readInt()
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(this.connectionRenderer);
        this.directConnections.toNetwork(buffer);
        GuiSprite.STREAM_CODEC.encode(buffer, this.entryNameBackground);
        buffer.writeInt(this.entryNameBackgroundTint);
        buffer.writeInt(this.entryNameTextColor);
    }
}
