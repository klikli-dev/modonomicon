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
import net.minecraft.world.item.crafting.SmithingRecipe;

public class BookSmithingRecipePage extends BookRecipePage<SmithingRecipe> {

    public static final Identifier ID = Modonomicon.loc("smithing_recipe");
    public static final MapCodec<BookSmithingRecipePage> CODEC = codec(BookSmithingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSmithingRecipePage> STREAM_CODEC = streamCodec(BookSmithingRecipePage::new);

    public BookSmithingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookSmithingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.SMITHING_RECIPE;
    }
}
