/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.CategoryLinkBookEntry;
import com.klikli_dev.modonomicon.book.entries.EntryLinkBookEntry;
import com.klikli_dev.modonomicon.data.BookEntryType;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class BookEntryTypeRegistry {

    private static final DispatchCodecRegistry<BookEntryType<?>> TYPES = new DispatchCodecRegistry<>(BookEntryType::id, "entry type");

    public static final BookEntryType<BookContentEntry> CONTENT = register(BookContentEntry.ID, BookContentEntry.CODEC, BookContentEntry.STREAM_CODEC);

    public static final BookEntryType<CategoryLinkBookEntry> CATEGORY_LINK = register(CategoryLinkBookEntry.ID, CategoryLinkBookEntry.CODEC, CategoryLinkBookEntry.STREAM_CODEC);

    public static final BookEntryType<EntryLinkBookEntry> ENTRY_LINK = register(EntryLinkBookEntry.ID, EntryLinkBookEntry.CODEC, EntryLinkBookEntry.STREAM_CODEC);

    private BookEntryTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends BookEntry> BookEntryType<T> register(Identifier id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, new BookEntryType<>(id, codec, streamCodec));
    }

    public static Codec<BookEntryType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, BookEntryType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
