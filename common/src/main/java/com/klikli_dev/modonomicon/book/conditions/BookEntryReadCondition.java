/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
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

public class BookEntryReadCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("entry_read");
    public static final MapCodec<BookEntryReadCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codecs.STRICT_IDENTIFIER.fieldOf("entry_id").forGetter(condition -> condition.entryId)
    ).apply(instance, (tooltip, entryId) -> new BookEntryReadCondition(tooltip.orElse(null), entryId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryReadCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.entryId,
            (tooltip, entryId) -> new BookEntryReadCondition(tooltip.orElse(null), entryId)
    );

    protected Identifier entryId;

    public BookEntryReadCondition(Component tooltip, Identifier entryId) {
        super(tooltip);
        this.entryId = entryId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.ENTRY_READ;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        var entry = context.getBook().getEntry(this.entryId);
        if (entry == null) {
            throw new IllegalArgumentException("Entry with id " + this.entryId + " not found in book " + context.getBook().getId() + "for BookEntryReadCondition. This happened while trying to unlock " + context);
        }
        return BookUnlockStateManager.get().isReadFor(player, entry);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_ENTRY_READ, Component.translatable(entryContext.getBook().getEntry(this.entryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
