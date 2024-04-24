/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BookIconModel {
    private final ItemStack itemStack;
    private final ResourceLocation texture;

    private final int width;
    private final int height;

    protected BookIconModel(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.texture = null;
        this.width = ModonomiconConstants.Data.Icon.DEFAULT_WIDTH;
        this.height = ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT;
    }

    protected BookIconModel(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.itemStack = ItemStack.EMPTY;
        this.width = width;
        this.height = height;
    }

    public static BookIconModel create(ItemLike item) {
        return new BookIconModel(new ItemStack(item));
    }

    public static BookIconModel create(ItemStack stack) {
        return new BookIconModel(stack);
    }

    public static BookIconModel create(ResourceLocation texture) {
        return create(texture, ModonomiconConstants.Data.Icon.DEFAULT_WIDTH, ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT);
    }

    public static BookIconModel create(ResourceLocation texture, int width, int height) {
        return new BookIconModel(texture, width, height);
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        if (this.texture != null) {
            JsonObject json = new JsonObject();
            json.addProperty("texture", this.texture.toString());
            json.addProperty("width", this.width);
            json.addProperty("height", this.height);
            return json;
        } else {
            JsonObject json = new JsonObject();
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString());
            return json;
        }
    }
}