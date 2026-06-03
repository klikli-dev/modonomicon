/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Defines a research node that unlocks when all required facts are present
 * AND all required values have reached their thresholds.
 *
 * @param id unique identifier for this node
 * @param requiredFacts fact ids that must be granted for this node to unlock
 * @param requiredValues value requirements that must be met for this node to unlock
 * @param toast optional toast display data
 */
public record ResearchNodeDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ValueRequirement> requiredValues,
        Optional<ResearchToastDefinition> toast
) {
    public record ValueRequirement(Identifier valueId, int threshold) {
        public static final Codec<ValueRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("value_id").forGetter(ValueRequirement::valueId),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("threshold").forGetter(ValueRequirement::threshold)
        ).apply(instance, ValueRequirement::new));
    }

    public static final Codec<ResearchNodeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchNodeDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchNodeDefinition::requiredFacts),
            ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchNodeDefinition::requiredValues),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchNodeDefinition::toastOrEmpty)
    ).apply(instance, (id, requiredFacts, requiredValues, toast) -> new ResearchNodeDefinition(id, requiredFacts, requiredValues, toast)));

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }
}
