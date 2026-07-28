/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record ResearchFactDefinition(
        Identifier id,
        Optional<ResearchToastDefinition> toast
) {
    public static final Codec<ResearchFactDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchFactDefinition::id),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchFactDefinition::toastOrEmpty)
    ).apply(instance, (id, toast) -> new ResearchFactDefinition(id, toast)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchFactDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchFactDefinition::id,
                    ByteBufCodecs.optional(ResearchToastDefinition.STREAM_CODEC),
                    ResearchFactDefinition::toast,
                    ResearchFactDefinition::new
            );

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }
}
