/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * A reward granted to a player once when a research stage completes, see {@code ResearchStageDefinition#rewards}.
 * Rewards are granted at most once per stage and player, tracked in {@code PlayerResearchState}.
 */
public interface ResearchReward {

    Codec<ResearchReward> CODEC = ResearchRewardTypeRegistry.codec()
            .dispatch("type", ResearchReward::type, ResearchRewardType::codec);

    StreamCodec<RegistryFriendlyByteBuf, ResearchReward> STREAM_CODEC = ResearchRewardTypeRegistry.streamCodec()
            .dispatch(ResearchReward::type, ResearchRewardType::streamCodec);

    ResearchRewardType<?> type();

    /**
     * Grants the reward to the player. Only called on the server.
     */
    void grant(ServerPlayer player);
}
