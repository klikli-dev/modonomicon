/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.BookAdvancementCondition;
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCategoryHasVisibleEntriesCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition;
import com.klikli_dev.modonomicon.book.conditions.BookFalseCondition;
import com.klikli_dev.modonomicon.book.conditions.BookModLoadedCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.conditions.BookOrCondition;
import com.klikli_dev.modonomicon.book.conditions.BookTrueCondition;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class BookConditionTypeRegistry {

    private static final DispatchCodecRegistry<BookConditionType<?>> TYPES = new DispatchCodecRegistry<>(BookConditionType::id, "condition type");

    public static final BookConditionType<BookNoneCondition> NONE = register(BookNoneCondition.ID, BookNoneCondition.CODEC, BookNoneCondition.STREAM_CODEC);

    public static final BookConditionType<BookAdvancementCondition> ADVANCEMENT = register(BookAdvancementCondition.ID, BookAdvancementCondition.CODEC, BookAdvancementCondition.STREAM_CODEC);

    public static final BookConditionType<BookEntryUnlockedCondition> ENTRY_UNLOCKED = register(BookEntryUnlockedCondition.ID, BookEntryUnlockedCondition.CODEC, BookEntryUnlockedCondition.STREAM_CODEC);

    public static final BookConditionType<BookEntryReadCondition> ENTRY_READ = register(BookEntryReadCondition.ID, BookEntryReadCondition.CODEC, BookEntryReadCondition.STREAM_CODEC);

    public static final BookConditionType<BookOrCondition> OR = register(BookOrCondition.ID, BookOrCondition.CODEC, BookOrCondition.STREAM_CODEC);

    public static final BookConditionType<BookAndCondition> AND = register(BookAndCondition.ID, BookAndCondition.CODEC, BookAndCondition.STREAM_CODEC);

    public static final BookConditionType<BookTrueCondition> TRUE = register(BookTrueCondition.ID, BookTrueCondition.CODEC, BookTrueCondition.STREAM_CODEC);

    public static final BookConditionType<BookFalseCondition> FALSE = register(BookFalseCondition.ID, BookFalseCondition.CODEC, BookFalseCondition.STREAM_CODEC);

    public static final BookConditionType<BookModLoadedCondition> MOD_LOADED = register(BookModLoadedCondition.ID, BookModLoadedCondition.CODEC, BookModLoadedCondition.STREAM_CODEC);

    public static final BookConditionType<BookCategoryHasVisibleEntriesCondition> CATEGORY_HAS_VISIBLE_ENTRIES = register(BookCategoryHasVisibleEntriesCondition.ID, BookCategoryHasVisibleEntriesCondition.CODEC, BookCategoryHasVisibleEntriesCondition.STREAM_CODEC);

    private BookConditionTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends BookCondition> BookConditionType<T> register(Identifier id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, new BookConditionType<>(id, codec, streamCodec));
    }

    public static Codec<BookConditionType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, BookConditionType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
