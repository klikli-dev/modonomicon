/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.registry.ResearchActionTypeRegistry;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Pays the cost and grants the fact the action is attached to ("pay to learn").
 *
 * @param cost the cost the player pays
 */
public record GrantFactAction(ResearchCost cost) implements ResearchAction {

    public static final MapCodec<GrantFactAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchCost.CODEC.optionalFieldOf("cost", ResearchCost.EMPTY).forGetter(GrantFactAction::cost)
    ).apply(instance, GrantFactAction::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantFactAction> STREAM_CODEC =
            StreamCodec.composite(
                    ResearchCost.STREAM_CODEC,
                    GrantFactAction::cost,
                    GrantFactAction::new
            );

    @Override
    public ResearchActionType<?> type() {
        return ResearchActionTypeRegistry.GRANT_FACT;
    }

    @Override
    public boolean isAvailable(ServerPlayer player, Identifier factId) {
        return true;
    }

    /**
     * Granting a fact is meaningless once it is granted, so this action never repeats.
     */
    @Override
    public boolean repeatable() {
        return false;
    }

    @Override
    public void execute(ServerPlayer player, Identifier factId) {
        ResearchServices.state().grantFact(player, factId);
    }
}
