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
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
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

public class BookResearchStageCompletedCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("research_stage_completed");
    public static final MapCodec<BookResearchStageCompletedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codecs.STRICT_IDENTIFIER.fieldOf("node_id").forGetter(condition -> condition.nodeId),
            Codecs.STRICT_IDENTIFIER.fieldOf("stage_id").forGetter(condition -> condition.stageId)
    ).apply(instance, (tooltip, nodeId, stageId) -> new BookResearchStageCompletedCondition(tooltip.orElse(null), nodeId, stageId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookResearchStageCompletedCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.nodeId,
            Identifier.STREAM_CODEC, condition -> condition.stageId,
            (tooltip, nodeId, stageId) -> new BookResearchStageCompletedCondition(tooltip.orElse(null), nodeId, stageId)
    );

    protected Identifier nodeId;
    protected Identifier stageId;

    public Identifier nodeId() {
        return this.nodeId;
    }

    public Identifier stageId() {
        return this.stageId;
    }

    public BookResearchStageCompletedCondition(Component tooltip, Identifier nodeId, Identifier stageId) {
        super(tooltip);
        this.nodeId = nodeId;
        this.stageId = stageId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.RESEARCH_STAGE_COMPLETED;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (!ResearchDataManager.get().data().stageIds().contains(this.stageId)) {
            throw new IllegalArgumentException("Unknown stage '" + this.stageId + "' referenced by book condition '" + ID + "'.");
        }
        return ResearchServices.state().isStageCompleted(player, this.nodeId, this.stageId);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_RESEARCH_STAGE_COMPLETED, Component.literal(this.nodeId.toString()), Component.literal(this.stageId.toString()));
        }
        return super.getTooltip(player, context);
    }
}
