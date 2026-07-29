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
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class BookSmeltingRecipePage extends BookProcessingRecipePage<SmeltingRecipe> {

    public static final Identifier ID = Modonomicon.loc("smelting_recipe");
    public static final MapCodec<BookSmeltingRecipePage> CODEC = codec(BookSmeltingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSmeltingRecipePage> STREAM_CODEC = streamCodec(BookSmeltingRecipePage::new);

    public BookSmeltingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookSmeltingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.SMELTING_RECIPE;
    }
}
