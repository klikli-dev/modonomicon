/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.research.ResearchServices;
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

public class BookResearchNodeUnlockedCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("research_node_unlocked");
    public static final MapCodec<BookResearchNodeUnlockedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codecs.STRICT_IDENTIFIER.fieldOf("node_id").forGetter(condition -> condition.nodeId)
    ).apply(instance, (tooltip, nodeId) -> new BookResearchNodeUnlockedCondition(tooltip.orElse(null), nodeId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookResearchNodeUnlockedCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.nodeId,
            (tooltip, nodeId) -> new BookResearchNodeUnlockedCondition(tooltip.orElse(null), nodeId)
    );

    protected Identifier nodeId;

    public BookResearchNodeUnlockedCondition(Component tooltip, Identifier nodeId) {
        super(tooltip);
        this.nodeId = nodeId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.RESEARCH_NODE_UNLOCKED;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return ResearchServices.state().isNodeUnlocked(player, this.nodeId);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_RESEARCH_NODE_UNLOCKED, Component.literal(this.nodeId.toString()));
        }
        return super.getTooltip(player, context);
    }
}
