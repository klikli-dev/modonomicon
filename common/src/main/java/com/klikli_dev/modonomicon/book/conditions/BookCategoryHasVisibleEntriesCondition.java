/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

/**
 * This BookCondition evaluates to true
 * if a category has at least
 */
public class BookCategoryHasVisibleEntriesCondition extends BookCondition {
    public static final Identifier ID = Modonomicon.loc("category_has_visible_entries");

    public static final MapCodec<BookCategoryHasVisibleEntriesCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip)),
            Identifier.CODEC.fieldOf("category_id").forGetter(condition -> condition.categoryId)
    ).apply(instance, (tooltip, categoryId) -> new BookCategoryHasVisibleEntriesCondition(tooltip.orElse(null), categoryId)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookCategoryHasVisibleEntriesCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip),
            Identifier.STREAM_CODEC, condition -> condition.categoryId,
            (tooltip, categoryId) -> new BookCategoryHasVisibleEntriesCondition(tooltip.orElse(null), categoryId)
    );

    protected Identifier categoryId;
    
    public BookCategoryHasVisibleEntriesCondition(Component tooltip, Identifier categoryId) {
        super(tooltip);
        this.categoryId = categoryId;
    }
    
    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.CATEGORY_HAS_VISIBLE_ENTRIES;
    }
    
    @Override
    public boolean test(BookConditionContext context, Player player) {
        var category = context.book.getCategory(this.categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with id " + this.categoryId + " not found in book " + context.getBook().getId() + "for BookCategoryHasVisibleEntriesCondition. This happened while trying to unlock " + context);
        }

        if(category.getEntries().isEmpty()) {
            return false;
        }

        for(var entry : category.getEntries().values()) {
            if(entry.getEntryDisplayState(player).isVisible()) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES, Component.translatable(entryContext.getBook().getEntry(this.categoryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
