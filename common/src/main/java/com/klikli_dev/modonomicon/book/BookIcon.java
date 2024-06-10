/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BookIcon {
    private final ItemStack itemStack;
    private final ResourceLocation texture;

    private final int width;
    private final int height;

    public BookIcon(ItemStack stack) {
        this.itemStack = stack;
        this.texture = null;
        this.width = ModonomiconConstants.Data.Icon.DEFAULT_WIDTH;
        this.height = ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT;
    }

    public BookIcon(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.itemStack = ItemStack.EMPTY;
        this.width = width;
        this.height = height;
    }

    public static BookIcon fromJson(JsonElement jsonElement) {
        //if string -> use from string
        //if json object -> parse from json
        if (jsonElement.isJsonPrimitive()) {
            return fromString(ResourceLocation.parse(jsonElement.getAsString()));
        }

        var jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("item")) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "item")));
            return new BookIcon(new ItemStack(item));
        } else if (jsonObject.has("texture")) {
            var width = GsonHelper.getAsInt(jsonObject, "width", ModonomiconConstants.Data.Icon.DEFAULT_WIDTH);
            var height = GsonHelper.getAsInt(jsonObject, "height", ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
            var texture = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "texture"));
            return new BookIcon(texture, width, height);
        } else {
            throw new JsonParseException("BookIcon must have either item or texture defined." + jsonElement);
        }
    }

    private static BookIcon fromString(ResourceLocation value) {
        if (value.getPath().endsWith(".png")) {
            return new BookIcon(value, ModonomiconConstants.Data.Icon.DEFAULT_WIDTH, ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
        } else {
            Item item = BuiltInRegistries.ITEM.get(value);
            return new BookIcon(new ItemStack(item));
        }
    }

    public static BookIcon fromNetwork(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            ResourceLocation texture = buffer.readResourceLocation();
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            return new BookIcon(texture, width, height);
        }

        ResourceLocation rl = buffer.readResourceLocation();
        Item item = BuiltInRegistries.ITEM.get(rl);
        return new BookIcon(new ItemStack(item));
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        if (this.texture != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(this.texture, x, y, 16, 16, 0, 0,this.width, this.height, this.width, this.height);
        } else {
            guiGraphics.renderItem(this.itemStack, x, y);
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.texture != null);
        if (this.texture != null) {
            buffer.writeResourceLocation(this.texture);
            buffer.writeVarInt(this.width);
            buffer.writeVarInt(this.height);
        } else {
            buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()));
        }
    }
}
