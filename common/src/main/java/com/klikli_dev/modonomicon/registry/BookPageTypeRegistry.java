/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.book.page.BookBlastingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookCampfireCookingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookCraftingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookEmptyPage;
import com.klikli_dev.modonomicon.book.page.BookEntityPage;
import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSmeltingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSmokingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.book.page.BookStonecuttingRecipePage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class BookPageTypeRegistry {

    private static final DispatchCodecRegistry<BookPageType<?>> TYPES = new DispatchCodecRegistry<>(BookPageType::id, "page type");

    public static final BookPageType<BookTextPage> TEXT = register(BookTextPage.ID, BookTextPage.CODEC, BookTextPage.STREAM_CODEC);

    public static final BookPageType<BookMultiblockPage> MULTIBLOCK = register(BookMultiblockPage.ID, BookMultiblockPage.CODEC, BookMultiblockPage.STREAM_CODEC);

    public static final BookPageType<BookCraftingRecipePage> CRAFTING_RECIPE = register(BookCraftingRecipePage.ID, BookCraftingRecipePage.CODEC, BookCraftingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookSmeltingRecipePage> SMELTING_RECIPE = register(BookSmeltingRecipePage.ID, BookSmeltingRecipePage.CODEC, BookSmeltingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookSmokingRecipePage> SMOKING_RECIPE = register(BookSmokingRecipePage.ID, BookSmokingRecipePage.CODEC, BookSmokingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookCampfireCookingRecipePage> CAMPFIRE_COOKING_RECIPE = register(BookCampfireCookingRecipePage.ID, BookCampfireCookingRecipePage.CODEC, BookCampfireCookingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookBlastingRecipePage> BLASTING_RECIPE = register(BookBlastingRecipePage.ID, BookBlastingRecipePage.CODEC, BookBlastingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookStonecuttingRecipePage> STONECUTTING_RECIPE = register(BookStonecuttingRecipePage.ID, BookStonecuttingRecipePage.CODEC, BookStonecuttingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookSmithingRecipePage> SMITHING_RECIPE = register(BookSmithingRecipePage.ID, BookSmithingRecipePage.CODEC, BookSmithingRecipePage.STREAM_CODEC);

    public static final BookPageType<BookSpotlightPage> SPOTLIGHT = register(BookSpotlightPage.ID, BookSpotlightPage.CODEC, BookSpotlightPage.STREAM_CODEC);

    public static final BookPageType<BookEmptyPage> EMPTY = register(BookEmptyPage.ID, BookEmptyPage.CODEC, BookEmptyPage.STREAM_CODEC);

    public static final BookPageType<BookEntityPage> ENTITY = register(BookEntityPage.ID, BookEntityPage.CODEC, BookEntityPage.STREAM_CODEC);

    public static final BookPageType<BookImagePage> IMAGE = register(BookImagePage.ID, BookImagePage.CODEC, BookImagePage.STREAM_CODEC);

    private BookPageTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends BookPage> BookPageType<T> register(Identifier id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, new BookPageType<>(id, codec, streamCodec));
    }

    public static Codec<BookPageType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, BookPageType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
