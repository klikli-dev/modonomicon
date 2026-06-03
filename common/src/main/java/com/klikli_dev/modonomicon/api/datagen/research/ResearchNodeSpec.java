/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Authoring-time node declaration that compiles into the canonical research node resource shape.
 *
 * @param ref the typed node ref being declared
 * @param requiredFacts the fact refs that act as this node's fact unlock requirements
 * @param requiredValues the value requirements that must be met for this node to unlock
 */
public record ResearchNodeSpec(
        ResearchNodeRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ValueRequirement> requiredValues
) {
    /**
     * Value requirement for a research node.
     *
     * @param valueRef the value that must reach the threshold
     * @param threshold the minimum value needed to satisfy this requirement
     */
    public record ValueRequirement(ResearchValueRef valueRef, int threshold) {
    }

    /**
     * Creates a node spec with only fact requirements.
     */
    public static ResearchNodeSpec factsOnly(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchNodeSpec(ref, requiredFacts, List.of());
    }

    /**
     * Compiles this authoring spec into the runtime/datapack node definition record.
     */
    public ResearchNodeDefinition toDefinition() {
        return new ResearchNodeDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList(),
                this.requiredValues.stream().map(req ->
                        new ResearchNodeDefinition.ValueRequirement(req.valueRef.id(), req.threshold())
                ).toList()
        );
    }
}
