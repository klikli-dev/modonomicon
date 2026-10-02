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
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class BookEmptyPage extends BookPage {

    public static final Identifier ID = Modonomicon.loc("empty");
    public static final MapCodec<BookEmptyPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition),
            BookPage.ASSOCIATED_ITEMS_CODEC.optionalFieldOf("associated_items", List.of()).forGetter(BookPage::getAssociatedItems)
    ).apply(instance, (id, condition, associatedItems) -> {
        var page = new BookEmptyPage(id, condition);
        page.setAssociatedItems(associatedItems);
        return page;
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookEmptyPage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookPage.ASSOCIATED_ITEMS_STREAM_CODEC, BookPage::getAssociatedItems,
            (id, condition, associatedItems) -> {
                var page = new BookEmptyPage(id, condition);
                page.setAssociatedItems(associatedItems);
                return page;
            }
    );

    public BookEmptyPage(String id, BookCondition condition) {
        super(id, condition);
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
