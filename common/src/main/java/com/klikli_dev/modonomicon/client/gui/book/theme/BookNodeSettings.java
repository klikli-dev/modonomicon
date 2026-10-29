/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * Theme-level settings controlling how node connections are rendered.
 *
 * @param connectionRenderer selects whether node connections use the existing routed sprite renderer or the direct line renderer.
 * @param directConnections configures the direct line renderer when {@link #connectionRenderer()} is set to {@link NodeConnectionRendererType#DIRECT}.
 * @param entryNameStyle default styling for permanently rendered entry names (see {@code render_name}).
 */
public record BookNodeSettings(NodeConnectionRendererType connectionRenderer, BookDirectConnectionTheme directConnections,
                               BookNodeNameStyle entryNameStyle) {
    public static final BookNodeSettings DEFAULT = new BookNodeSettings(
            NodeConnectionRendererType.SPRITE,
            BookDirectConnectionTheme.DEFAULT,
            BookNodeNameStyle.DEFAULT
    );

    public static BookNodeSettings fromJson(JsonObject json) {
        return new BookNodeSettings(
                NodeConnectionRendererType.fromString(GsonHelper.getAsString(json, "connection_renderer", DEFAULT.connectionRenderer.serializedName())),
                json.has("direct_connections") ? BookDirectConnectionTheme.fromJson(GsonHelper.getAsJsonObject(json, "direct_connections")) : DEFAULT.directConnections,
                json.has("entry_name_style") ? BookNodeNameStyle.fromJson(GsonHelper.getAsJsonObject(json, "entry_name_style")) : DEFAULT.entryNameStyle
        );
    }

    public static BookNodeSettings fromNetwork(RegistryFriendlyByteBuf buffer) {
        //NB: explicit varint ordinals instead of the deprecated FriendlyByteBuf#readEnum; identical wire format
        return new BookNodeSettings(
                NodeConnectionRendererType.values()[buffer.readVarInt()],
                BookDirectConnectionTheme.fromNetwork(buffer),
                BookNodeNameStyle.fromNetwork(buffer)
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        //NB: explicit varint ordinal instead of the deprecated FriendlyByteBuf#writeEnum; identical wire format
        buffer.writeVarInt(this.connectionRenderer.ordinal());
        this.directConnections.toNetwork(buffer);
        this.entryNameStyle.toNetwork(buffer);
    }
}
