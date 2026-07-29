/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchStageDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Authoring-time stage declaration that compiles into the canonical stage definition.
 *
 * @param ref the typed stage ref being declared
 * @param requiredFacts the fact refs required to advance past this stage
 * @param requiredValues the value requirements required to advance past this stage
 * @param toast optional toast display data for stage completion
 */
public record ResearchStageSpec(
        ResearchStageRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ResearchNodeSpec.ValueRequirement> requiredValues,
        Optional<ResearchToastDefinition> toast
) {
    public static ResearchStageSpec factsOnly(ResearchStageRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchStageSpec(ref, requiredFacts, List.of(), Optional.empty());
    }

    public static ResearchStageSpec valuesOnly(ResearchStageRef ref, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        return new ResearchStageSpec(ref, List.of(), requiredValues, Optional.empty());
    }

    public static ResearchStageSpec none(ResearchStageRef ref) {
        return new ResearchStageSpec(ref, List.of(), List.of(), Optional.empty());
    }

    /**
     * Adds toast display data to this stage spec.
     */
    public ResearchStageSpec toast(ResearchToastDefinition toast) {
        return new ResearchStageSpec(this.ref, this.requiredFacts, this.requiredValues, Optional.of(toast));
    }

    public ResearchStageDefinition toDefinition() {
        return new ResearchStageDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList(),
                this.requiredValues.stream().map(req ->
                        new ResearchNodeDefinition.ValueRequirement(req.valueRef().id(), req.threshold())
                ).toList(),
                this.toast
        );
    }
}
