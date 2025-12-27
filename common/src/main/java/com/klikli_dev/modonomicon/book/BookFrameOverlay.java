/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

/**
 * Allows configuring the rendering of a book frame overlay.
 * These frame overlays are rendered at the center of each frame side and allow to add non-repeating elements.
 */
public record BookFrameOverlay(Identifier texture, int textureWidth, int textureHeight, int frameWidth, int frameHeight,
                               int frameXOffset, int frameYOffset) {

    public static final Codec<BookFrameOverlay> CODEC = RecordCodecBuilder.create((builder) -> {
        return builder.group(
                Identifier.CODEC.fieldOf("texture").forGetter((overlay) -> {
                    return overlay.texture;
                }),
                Codec.SHORT.fieldOf("texture_width").forGetter((overlay) -> {
                    return (short) overlay.textureWidth;
                }),
                Codec.SHORT.fieldOf("texture_height").forGetter((overlay) -> {
                    return (short) overlay.textureHeight;
                }),
                Codec.SHORT.fieldOf("frame_width").forGetter((overlay) -> {
                    return (short) overlay.frameWidth;
                }),
                Codec.SHORT.fieldOf("frame_height").forGetter((overlay) -> {
                    return (short) overlay.frameHeight;
                }),
                Codec.SHORT.fieldOf("frame_x_offset").forGetter((overlay) -> {
                    return (short) overlay.frameXOffset;
                }),
                Codec.SHORT.fieldOf("frame_y_offset").forGetter((overlay) -> {
                    return (short) overlay.frameYOffset;
                })
        ).apply(builder, BookFrameOverlay::new);
    });

    public static BookFrameOverlay fromJson(JsonObject json) {
        return BookFrameOverlay.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    public static BookFrameOverlay fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readLenientJsonWithCodec(BookFrameOverlay.CODEC);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(BookFrameOverlay.CODEC, this);
    }

    public int getFrameU() {
        return this.textureWidth / 2 - this.frameWidth / 2;
    }

    public int getFrameV() {
        return this.textureHeight / 2 - this.frameHeight / 2;
    }

    public int getFrameX(int startX) {
        return startX - this.frameWidth / 2 + this.frameXOffset;
    }

    public int getFrameY(int startY) {
        return startY - this.frameHeight / 2 + this.frameYOffset;
    }
}
