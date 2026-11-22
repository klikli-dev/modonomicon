/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
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
public record AddValueEffect(Identifier valueId, int amount) implements ResearchEffect {

    public static final MapCodec<AddValueEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("value").forGetter(AddValueEffect::valueId),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(AddValueEffect::amount)
    ).apply(instance, AddValueEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddValueEffect> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    AddValueEffect::valueId,
                    ByteBufCodecs.VAR_INT,
                    AddValueEffect::amount,
                    AddValueEffect::new
            );

    @Override
    public ResearchEffectType<?> type() {
        return ResearchEffectTypeRegistry.ADD_VALUE;
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        return ResearchServices.state().incrementValue(player, this.valueId, this.amount);
    }
}
