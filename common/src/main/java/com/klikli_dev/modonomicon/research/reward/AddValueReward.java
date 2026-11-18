/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Adds an amount to a research value of the player.
 *
 * @param valueId the value to increase
 * @param amount the amount to add, must be positive
 */
public record AddValueReward(Identifier valueId, int amount) implements ResearchReward {

    public static final MapCodec<AddValueReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("value").forGetter(AddValueReward::valueId),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(AddValueReward::amount)
    ).apply(instance, AddValueReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddValueReward> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    AddValueReward::valueId,
                    ByteBufCodecs.VAR_INT,
                    AddValueReward::amount,
                    AddValueReward::new
            );

    @Override
    public ResearchRewardType<?> type() {
        return ResearchRewardTypeRegistry.ADD_VALUE;
    }

    @Override
    public void grant(ServerPlayer player) {
        ResearchServices.state().incrementValue(player, this.valueId, this.amount);
    }
}
