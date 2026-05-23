/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class BookEmptyPage extends BookPage {

    public static final Identifier ID = Modonomicon.loc("empty");
    public static final MapCodec<BookEmptyPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("anchor", "").forGetter(BookPage::getAnchor),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, BookEmptyPage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookEmptyPage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BookPage::getAnchor,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookEmptyPage::new
    );

    public BookEmptyPage(String anchor, BookCondition condition) {
        super(anchor, condition);
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.EMPTY;
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return false;
    }
}
