/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.util.function.Function;

public class GuiSprite {

    public static final GuiSprite EMPTY = new GuiSprite(Identifier.fromNamespaceAndPath("minecraft", "missingno"), 0, 0);
    private static final Codec<GuiSprite> SIZED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("sprite").forGetter(GuiSprite::sprite),
            Codec.INT.optionalFieldOf("width", -1).forGetter(GuiSprite::rawWidth),
            Codec.INT.optionalFieldOf("height", -1).forGetter(GuiSprite::rawHeight)
    ).apply(instance, (sprite, width, height) -> new GuiSprite(sprite, width, height)));
    public static final Codec<GuiSprite> CODEC = Codec.either(
            Identifier.CODEC,
            SIZED_CODEC
    ).xmap(value -> value.map(sprite -> new GuiSprite(sprite, -1, -1), Function.identity()), sprite -> {
        if (sprite.rawWidth() < 0 && sprite.rawHeight() < 0) {
            return Either.left(sprite.sprite());
        }
        return Either.right(sprite);
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, GuiSprite> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Identifier sprite;
    private final int tint;
    private int width;
    private int height;

    public GuiSprite(Identifier sprite, int width, int height) {
        this(sprite, width, height, -1);
    }

    private GuiSprite(Identifier sprite, int width, int height, int tint) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        this.tint = tint;
    }

    public static GuiSprite fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new GuiSprite(Identifier.parse(jsonElement.getAsString()), -1, -1);
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Identifier sprite = Identifier.parse(GsonHelper.getAsString(jsonObject, "sprite"));
        int width = GsonHelper.getAsInt(jsonObject, "width", -1);
        int height = GsonHelper.getAsInt(jsonObject, "height", -1);
        return new GuiSprite(sprite, width, height);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("sprite", this.sprite.toString());
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        return json;
    }

    public static GuiSprite fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, this);
    }

    public Identifier sprite() {
        return this.sprite;
    }

    public GuiSprite sized(int width, int height) {
        return new GuiSprite(this.sprite, width, height, this.tint);
    }

    public GuiSprite tinted(int tint) {
        return new GuiSprite(this.sprite, this.width, this.height, tint);
    }

    public int width() {
        this.resolveSizeIfNeeded();
        return this.width;
    }

    public int height() {
        this.resolveSizeIfNeeded();
        return this.height;
    }

    public boolean isEmpty() {
        return this.width() <= 0 || this.height() <= 0;
    }

    public int rawWidth() {
        return this.width;
    }

    public int rawHeight() {
        return this.height;
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.extractRenderState(guiGraphics, x, y, this.width(), this.height());
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, x, y, width, height, this.tint);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, int tint) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, x, y, this.width(), this.height(), tint);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, int tint) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, x, y, width, height, tint);
    }

    private void resolveSizeIfNeeded() {
        if (this.width >= 0 && this.height >= 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getAtlasManager() == null) {
            return;
        }

        TextureAtlasSprite sprite = minecraft.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(this.sprite);
        this.width = sprite.contents().width();
        this.height = sprite.contents().height();
    }
}
