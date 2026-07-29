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
import net.minecraft.world.item.crafting.SmokingRecipe;

public class BookSmokingRecipePage extends BookProcessingRecipePage<SmokingRecipe> {

    public static final Identifier ID = Modonomicon.loc("smoking_recipe");
    public static final MapCodec<BookSmokingRecipePage> CODEC = codec(BookSmokingRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSmokingRecipePage> STREAM_CODEC = streamCodec(BookSmokingRecipePage::new);

    public BookSmokingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookSmokingRecipePage(NetworkDataHolder common) {
        super(common);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.SMOKING_RECIPE;
    }
}
