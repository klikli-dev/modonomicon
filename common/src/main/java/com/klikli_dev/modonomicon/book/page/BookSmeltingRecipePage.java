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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class BookSmeltingRecipePage extends BookProcessingRecipePage<SmeltingRecipe> {

    public BookSmeltingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookSmeltingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    public static BookSmeltingRecipePage fromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(entryId, json, provider);
        return new BookSmeltingRecipePage(common);
    }

    public static BookSmeltingRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        return new BookSmeltingRecipePage(common);
    }

    @Override
    public Identifier getType() {
        return Page.SMELTING_RECIPE;
    }
}
