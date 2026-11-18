/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Grants a research fact to the player.
 *
 * @param factId the fact to grant
 */
public record GrantFactReward(Identifier factId) implements ResearchReward {

    public static final MapCodec<GrantFactReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("fact").forGetter(GrantFactReward::factId)
    ).apply(instance, GrantFactReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantFactReward> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    GrantFactReward::factId,
                    GrantFactReward::new
            );

    @Override
    public ResearchRewardType<?> type() {
        return ResearchRewardTypeRegistry.GRANT_FACT;
    }

    @Override
    public void grant(ServerPlayer player) {
        ResearchServices.state().grantFact(player, this.factId);
    }
}
