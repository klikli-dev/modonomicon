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
import net.minecraft.world.item.crafting.Recipe;

public class BookCraftingRecipePage extends BookRecipePage<Recipe<?>> {

    public static final Identifier ID = Modonomicon.loc("crafting_recipe");
    public static final MapCodec<BookCraftingRecipePage> CODEC = codec(BookCraftingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookCraftingRecipePage> STREAM_CODEC = streamCodec(BookCraftingRecipePage::new);

    public BookCraftingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookCraftingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.CRAFTING_RECIPE;
    }
}
