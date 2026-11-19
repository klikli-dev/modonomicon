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

/**
 * Canonical definition for one research value.
 *
 * @param id unique identifier for this value
 * @param toast optional toast display data
 * @param display optional display data used in requirement blocks
 */
public record ResearchValueDefinition(
        Identifier id,
        Optional<ResearchToastDefinition> toast,
        Optional<ResearchDisplay> display
) {
    public static final Codec<ResearchValueDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchValueDefinition::id),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchValueDefinition::toastOrEmpty),
            ResearchDisplay.CODEC.optionalFieldOf("display").forGetter(ResearchValueDefinition::displayOrEmpty)
    ).apply(instance, (id, toast, display) -> new ResearchValueDefinition(id, toast, display)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchValueDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchValueDefinition::id,
                    ByteBufCodecs.optional(ResearchToastDefinition.STREAM_CODEC),
                    ResearchValueDefinition::toast,
                    ByteBufCodecs.optional(ResearchDisplay.STREAM_CODEC),
                    ResearchValueDefinition::display,
                    ResearchValueDefinition::new
            );

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }

    public Optional<ResearchDisplay> displayOrEmpty() {
        return this.display;
    }
}
