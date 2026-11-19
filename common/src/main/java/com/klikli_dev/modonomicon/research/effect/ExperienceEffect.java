/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
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
public record ExperienceEffect(int amount, boolean levels) implements ResearchEffect {

    public static final MapCodec<ExperienceEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(ExperienceEffect::amount),
            Codec.BOOL.optionalFieldOf("levels", false).forGetter(ExperienceEffect::levels)
    ).apply(instance, ExperienceEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExperienceEffect> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ExperienceEffect::amount,
                    ByteBufCodecs.BOOL,
                    ExperienceEffect::levels,
                    ExperienceEffect::new
            );

    @Override
    public ResearchEffectType<?> type() {
        return ResearchEffectTypeRegistry.EXPERIENCE;
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        if (this.levels) {
            player.giveExperienceLevels(this.amount);
        } else {
            player.giveExperiencePoints(this.amount);
        }
        return true;
    }
}
