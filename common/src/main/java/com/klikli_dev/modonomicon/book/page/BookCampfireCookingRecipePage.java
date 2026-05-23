/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

public class BookCampfireCookingRecipePage extends BookProcessingRecipePage<CampfireCookingRecipe> {

    public static final Identifier ID = Modonomicon.loc("campfire_cooking_recipe");
    public static final MapCodec<BookCampfireCookingRecipePage> CODEC = codec(BookCampfireCookingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookCampfireCookingRecipePage> STREAM_CODEC = streamCodec(BookCampfireCookingRecipePage::new);

    public BookCampfireCookingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookCampfireCookingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.CAMPFIRE_COOKING_RECIPE;
    }
}
