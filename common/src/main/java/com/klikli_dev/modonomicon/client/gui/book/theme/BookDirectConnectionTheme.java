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
 * Data-driven configuration for the direct node connection renderer.
 *
 * @param width controls the rendered line width used for direct connections.
 * @param opacity multiplies the alpha of direct connections after their per-state colors are chosen.
 * @param brightness multiplies the RGB intensity of direct connections to improve visibility on dark backgrounds.
 * @param oscillation enables or disables the animated offset effect for non-settled direct connections.
 * @param oscillationAmplitude controls the peak pixel offset applied by the animated offset effect.
 * @param oscillationSpeed controls how quickly the animated offset effect advances over time.
 * @param connectedColor base ARGB color used for settled direct connections.
 * @param availableColor base ARGB color used when a parent is available but the child is not yet settled.
 * @param discoveredColor base ARGB color used when both ends are visible but not yet settled.
 */
public record BookDirectConnectionTheme(
        float width,
        float opacity,
        float brightness,
        boolean oscillation,
        float oscillationAmplitude,
        float oscillationSpeed,
        int connectedColor,
        int availableColor,
        int discoveredColor
) {
    public static final BookDirectConnectionTheme DEFAULT = new BookDirectConnectionTheme(
            2.25F,
            1.0F,
            1.75F,
            true,
            5.0F,
            1.0F,
            0xBFFFFFFF,
            0xFF00FF00,
            0xFF0000FF
    );

    public static BookDirectConnectionTheme fromJson(JsonObject json) {
        return new BookDirectConnectionTheme(
                GsonHelper.getAsFloat(json, "width", DEFAULT.width),
                GsonHelper.getAsFloat(json, "opacity", DEFAULT.opacity),
                GsonHelper.getAsFloat(json, "brightness", DEFAULT.brightness),
                GsonHelper.getAsBoolean(json, "oscillation", DEFAULT.oscillation),
                GsonHelper.getAsFloat(json, "oscillation_amplitude", DEFAULT.oscillationAmplitude),
                GsonHelper.getAsFloat(json, "oscillation_speed", DEFAULT.oscillationSpeed),
                GsonHelper.getAsInt(json, "connected_color", DEFAULT.connectedColor),
                GsonHelper.getAsInt(json, "available_color", DEFAULT.availableColor),
                GsonHelper.getAsInt(json, "discovered_color", DEFAULT.discoveredColor)
        );
    }

    public static BookDirectConnectionTheme fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookDirectConnectionTheme(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt()
        );
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.width);
        buffer.writeFloat(this.opacity);
        buffer.writeFloat(this.brightness);
        buffer.writeBoolean(this.oscillation);
        buffer.writeFloat(this.oscillationAmplitude);
        buffer.writeFloat(this.oscillationSpeed);
        buffer.writeInt(this.connectedColor);
        buffer.writeInt(this.availableColor);
        buffer.writeInt(this.discoveredColor);
    }
}
