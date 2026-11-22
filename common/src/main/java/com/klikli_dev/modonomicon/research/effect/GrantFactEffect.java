/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
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
public record GrantFactEffect(Identifier factId) implements ResearchEffect {

    public static final MapCodec<GrantFactEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("fact").forGetter(GrantFactEffect::factId)
    ).apply(instance, GrantFactEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantFactEffect> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    GrantFactEffect::factId,
                    GrantFactEffect::new
            );

    @Override
    public ResearchEffectType<?> type() {
        return ResearchEffectTypeRegistry.GRANT_FACT;
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        return ResearchServices.state().grantFact(player, this.factId);
    }
}
