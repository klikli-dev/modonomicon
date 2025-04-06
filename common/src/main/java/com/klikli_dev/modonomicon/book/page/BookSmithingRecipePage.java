/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmithingRecipe;

public class BookSmithingRecipePage extends BookRecipePage<SmithingRecipe> {

    public BookSmithingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookSmithingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    public static BookSmithingRecipePage fromJson(ResourceLocation entryId, JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(entryId, json, provider);
        return new BookSmithingRecipePage(common);
    }

    public static BookSmithingRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        return new BookSmithingRecipePage(common);
    }

    @Override
    public ResourceLocation getType() {
        return Page.SMITHING_RECIPE;
    }
}
