/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * Gives the player experience points, or experience levels if {@code levels} is true.
 *
 * @param amount the amount of points or levels to give, must be positive
 * @param levels whether the amount is in levels instead of points
 */
public record ExperienceReward(int amount, boolean levels) implements ResearchReward {

    public static final MapCodec<ExperienceReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(ExperienceReward::amount),
            Codec.BOOL.optionalFieldOf("levels", false).forGetter(ExperienceReward::levels)
    ).apply(instance, ExperienceReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExperienceReward> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ExperienceReward::amount,
                    ByteBufCodecs.BOOL,
                    ExperienceReward::levels,
                    ExperienceReward::new
            );

    @Override
    public ResearchRewardType<?> type() {
        return ResearchRewardTypeRegistry.EXPERIENCE;
    }

    @Override
    public void grant(ServerPlayer player) {
        if (this.levels) {
            player.giveExperienceLevels(this.amount);
        } else {
            player.giveExperiencePoints(this.amount);
        }
    }
}
