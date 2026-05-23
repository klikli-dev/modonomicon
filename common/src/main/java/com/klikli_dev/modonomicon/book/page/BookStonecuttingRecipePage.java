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
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class BookStonecuttingRecipePage extends BookProcessingRecipePage<StonecutterRecipe> {

    public static final Identifier ID = Modonomicon.loc("stonecutting_recipe");
    public static final MapCodec<BookStonecuttingRecipePage> CODEC = codec(BookStonecuttingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookStonecuttingRecipePage> STREAM_CODEC = streamCodec(BookStonecuttingRecipePage::new);

    public BookStonecuttingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookStonecuttingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.STONECUTTING_RECIPE;
    }
}
