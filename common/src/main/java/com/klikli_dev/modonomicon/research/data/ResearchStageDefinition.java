/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
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
 * @param display optional display data used in requirement blocks, e.g. the title of the stage
 * @param completion how the stage completes, automatic by default
 * @param effects effects granted once to the player when this stage completes
 */
public record ResearchStageDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ResearchNodeDefinition.ValueRequirement> requiredValues,
        Optional<ResearchToastDefinition> toast,
        Optional<ResearchDisplay> display,
        ResearchStageCompletion completion,
        List<ResearchEffect> effects
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
                    ByteBufCodecs.optional(ResearchDisplay.STREAM_CODEC),
                    ResearchStageDefinition::display,
                    ResearchStageCompletion.STREAM_CODEC,
                    ResearchStageDefinition::completion,
                    ByteBufCodecs.collection(ArrayList::new, ResearchEffectTypeRegistry.STREAM_CODEC),
                    ResearchStageDefinition::effects,
                    ResearchStageDefinition::new
            );

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }

    public Optional<ResearchDisplay> displayOrEmpty() {
        return this.display;
    }

    public static final Codec<ResearchStageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchStageDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchStageDefinition::requiredFacts),
            ResearchNodeDefinition.ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchStageDefinition::requiredValues),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchStageDefinition::toastOrEmpty),
            ResearchDisplay.CODEC.optionalFieldOf("display").forGetter(ResearchStageDefinition::displayOrEmpty),
            ResearchStageCompletion.CODEC.optionalFieldOf("completion", ResearchStageCompletion.AUTO).forGetter(ResearchStageDefinition::completion),
            ResearchEffectTypeRegistry.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(ResearchStageDefinition::effects)
    ).apply(instance, ResearchStageDefinition::new));
}
