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
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.bookstate.BookServices;
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
        if (context == null || context.book == null) {
            //Can happen if the book was not (yet) built, e.g. during /reload before books are rebuilt,
            //or if book loading failed. Never crash a server tick over this, fail closed and log.
            Modonomicon.LOG.error("BookCategoryHasVisibleEntriesCondition tested with null book for category '{}'. The book was likely not built yet (e.g. during /reload) or failed to load. Returning false to avoid a crash.", this.categoryId);
            return false;
        }

        var category = context.book.getCategory(this.categoryId);
        if (category == null) {
            //Do not throw here: this runs during server ticks (e.g. research hooks) and would crash the server.
            //Instead record a blocking book error so the book shows an error screen, log, and fail closed.
            var message = "Category with id " + this.categoryId + " not found in book " + context.getBook().getId() + " for BookCategoryHasVisibleEntriesCondition. This happened while trying to unlock " + context;
            Modonomicon.LOG.error(message);
            BookErrorManager.get().error(context.getBook().getId(), message, null, true);
            return false;
        }

        if(category.getEntries().isEmpty()) {
            return false;
        }

        for(var entry : category.getEntries().values()) {
            if (BookServices.visibility().isVisible(player, entry)) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext
                && entryContext.getBook() != null && entryContext.getBook().getEntry(this.categoryId) != null) {
            this.tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES, Component.translatable(entryContext.getBook().getEntry(this.categoryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
