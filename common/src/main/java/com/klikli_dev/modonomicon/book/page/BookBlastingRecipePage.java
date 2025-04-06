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
import net.minecraft.world.item.crafting.BlastingRecipe;

public class BookBlastingRecipePage extends BookProcessingRecipePage<BlastingRecipe> {

    public BookBlastingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookBlastingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    public static BookBlastingRecipePage fromJson(ResourceLocation entryId, JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(entryId, json, provider);
        return new BookBlastingRecipePage(common);
    }

    public static BookBlastingRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        return new BookBlastingRecipePage(common);
    }

    @Override
    public ResourceLocation getType() {
        return Page.BLASTING_RECIPE;
    }
}
