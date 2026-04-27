/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public class GuiTexture {

    public static final GuiTexture EMPTY = new GuiTexture(Identifier.fromNamespaceAndPath("minecraft", "missingno"), 0, 0);

    private final Identifier sprite;
    private int width;
    private int height;

    public GuiTexture(Identifier sprite, int width, int height) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
    }

    public static GuiTexture fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new GuiTexture(Identifier.parse(jsonElement.getAsString()), -1, -1);
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Identifier sprite = Identifier.parse(GsonHelper.getAsString(jsonObject, "sprite"));
        int width = GsonHelper.getAsInt(jsonObject, "width", -1);
        int height = GsonHelper.getAsInt(jsonObject, "height", -1);
        return new GuiTexture(sprite, width, height);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("sprite", this.sprite.toString());
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        return json;
    }

    public static GuiTexture fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new GuiTexture(buffer.readIdentifier(), buffer.readVarInt(), buffer.readVarInt());
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeIdentifier(this.sprite);
        buffer.writeVarInt(this.width);
        buffer.writeVarInt(this.height);
    }

    public Identifier sprite() {
        return this.sprite;
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
