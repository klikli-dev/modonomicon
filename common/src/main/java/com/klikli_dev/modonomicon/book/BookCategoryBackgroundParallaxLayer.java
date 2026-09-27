/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Category;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookCategoryBackgroundParallaxLayer {
    public static final Codec<BookCategoryBackgroundParallaxLayer> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(
                    Identifier.CODEC.fieldOf("background").forGetter((overlay) -> overlay.background),
                    Codec.FLOAT.optionalFieldOf("speed", 0.5f).forGetter((overlay) -> overlay.speed),
                    Codec.FLOAT.optionalFieldOf("vanishZoom", -1.0f).forGetter((overlay) -> overlay.vanishZoom),
                    BookBackgroundRenderingMode.CODEC.optionalFieldOf("background_rendering_mode", BookBackgroundRenderingMode.REPEAT).forGetter((overlay) -> overlay.renderingMode),
                    Codec.FLOAT.optionalFieldOf("background_overscan", Category.DEFAULT_BACKGROUND_OVERSCAN).forGetter((overlay) -> overlay.overscan)
            ).apply(builder, BookCategoryBackgroundParallaxLayer::new));

    /**
     * The texture to use for this layer.
     */
    protected Identifier background;

    /**
     * The speed at which this layer moves.
     */
    protected float speed;

    /**
     * The zoom level at which this layer vanishes.
     */
    protected float vanishZoom;

    /**
     * Configures how this layer is rendered. Repeat tiles the texture, scale stretches it to the
     * background area and fit covers the area without distortion.
     */
    protected BookBackgroundRenderingMode renderingMode;

    /**
     * Uniform extra zoom applied to scale and fit layers, rendering them larger than the background area.
     * The resulting margin allows the layer to pan with scrolling. 1.0 disables panning.
     * Ignored for repeat layers (those already pan by tiling).
     */
    protected float overscan;

    public BookCategoryBackgroundParallaxLayer(Identifier background) {
        this(background, 0.5f, -1.0f);
    }

    public BookCategoryBackgroundParallaxLayer(Identifier background, float speed, float vanishZoom) {
        this(background, speed, vanishZoom, BookBackgroundRenderingMode.REPEAT, Category.DEFAULT_BACKGROUND_OVERSCAN);
    }

    public BookCategoryBackgroundParallaxLayer(Identifier background, float speed, float vanishZoom, BookBackgroundRenderingMode renderingMode, float overscan) {
        this.background = background;
        this.speed = speed;
        this.vanishZoom = vanishZoom;
        this.renderingMode = renderingMode;
        this.overscan = overscan;
    }

    public static BookCategoryBackgroundParallaxLayer fromJson(JsonObject json) {
        return BookCategoryBackgroundParallaxLayer.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    public static List<BookCategoryBackgroundParallaxLayer> fromJson(JsonArray json) {
        return StreamSupport.stream(json.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(BookCategoryBackgroundParallaxLayer::fromJson)
                .collect(Collectors.toList());
    }

    public static BookCategoryBackgroundParallaxLayer fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readLenientJsonWithCodec(BookCategoryBackgroundParallaxLayer.CODEC);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(BookCategoryBackgroundParallaxLayer.CODEC, this);
    }

    public Identifier getBackground() {
        return this.background;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getVanishZoom() {
        return this.vanishZoom;
    }

    public BookBackgroundRenderingMode getRenderingMode() {
        return this.renderingMode;
    }

    public float getOverscan() {
        return this.overscan;
    }

}
