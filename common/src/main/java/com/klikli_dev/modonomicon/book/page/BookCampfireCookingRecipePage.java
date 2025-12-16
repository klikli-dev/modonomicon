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
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

public class BookCampfireCookingRecipePage extends BookProcessingRecipePage<CampfireCookingRecipe> {

    public BookCampfireCookingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookCampfireCookingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    public static BookCampfireCookingRecipePage fromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(entryId, json, provider);
        return new BookCampfireCookingRecipePage(common);
    }

    public static BookCampfireCookingRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        return new BookCampfireCookingRecipePage(common);
    }

    @Override
    public Identifier getType() {
        return Page.CAMPFIRE_COOKING_RECIPE;
    }
}
