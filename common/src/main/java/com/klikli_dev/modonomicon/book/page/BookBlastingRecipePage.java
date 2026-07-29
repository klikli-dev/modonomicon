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
import net.minecraft.world.item.crafting.BlastingRecipe;

public class BookBlastingRecipePage extends BookProcessingRecipePage<BlastingRecipe> {

    public static final Identifier ID = Modonomicon.loc("blasting_recipe");
    public static final MapCodec<BookBlastingRecipePage> CODEC = codec(BookBlastingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookBlastingRecipePage> STREAM_CODEC = streamCodec(BookBlastingRecipePage::new);

    public BookBlastingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookBlastingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.BLASTING_RECIPE;
    }
}
