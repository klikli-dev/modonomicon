/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Defines one sequential stage within a multi-stage research node.
 *
 * @param id unique identifier for this stage (used by conditions and stage dependencies)
 * @param requiredFacts fact ids that must be granted to advance past this stage
 * @param requiredValues value requirements that must be met to advance past this stage
 * @param toast optional toast display data for stage completion
 */
public record ResearchStageDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ResearchNodeDefinition.ValueRequirement> requiredValues,
        Optional<ResearchToastDefinition> toast
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchStageDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchStageDefinition::id,
                    ByteBufCodecs.collection(ArrayList::new, Identifier.STREAM_CODEC),
                    ResearchStageDefinition::requiredFacts,
                    ByteBufCodecs.collection(ArrayList::new, ResearchNodeDefinition.ValueRequirement.STREAM_CODEC),
                    ResearchStageDefinition::requiredValues,
                    ByteBufCodecs.optional(ResearchToastDefinition.STREAM_CODEC),
                    ResearchStageDefinition::toast,
                    ResearchStageDefinition::new
            );

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }

    public static final Codec<ResearchStageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchStageDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchStageDefinition::requiredFacts),
            ResearchNodeDefinition.ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchStageDefinition::requiredValues),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchStageDefinition::toastOrEmpty)
    ).apply(instance, ResearchStageDefinition::new));
}
