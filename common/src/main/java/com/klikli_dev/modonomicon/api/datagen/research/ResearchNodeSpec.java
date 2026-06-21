/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Authoring-time node declaration that compiles into the canonical research node resource shape.
 *
 * @param ref the typed node ref being declared
 * @param requiredFacts the fact refs that act as this node's fact unlock requirements
 * @param requiredValues the value requirements that must be met for this node to unlock
 * @param stages ordered stage specs; empty means single-stage
 * @param requiredStages dependencies on other nodes reaching specific stages
 * @param toast optional toast display data for full node completion
 */
public record ResearchNodeSpec(
        ResearchNodeRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ValueRequirement> requiredValues,
        List<ResearchStageSpec> stages,
        List<StageDependencySpec> requiredStages,
        Optional<ResearchToastDefinition> toast
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
     * Dependency on another node reaching a specific stage.
     */
    public record StageDependencySpec(ResearchNodeRef nodeId, ResearchStageRef stageId) {
        public static StageDependencySpec of(ResearchNodeRef nodeId, ResearchStageRef stageId) {
            return new StageDependencySpec(nodeId, stageId);
        }
    }

    /**
     * Creates a node spec with only fact requirements.
     */
    public static ResearchNodeSpec factsOnly(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchNodeSpec(ref, requiredFacts, List.of(), List.of(), List.of(), Optional.empty());
    }

    public static ResearchNodeSpec of(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<StageDependencySpec> requiredStages,
            Optional<ResearchToastDefinition> toast
    ) {
        return new ResearchNodeSpec(ref, requiredFacts, requiredValues, stages, requiredStages, toast);
    }

    /**
     * Adds toast display data to this node spec.
     */
    public ResearchNodeSpec toast(ResearchToastDefinition toast) {
        return new ResearchNodeSpec(this.ref, this.requiredFacts, this.requiredValues, this.stages, this.requiredStages, Optional.of(toast));
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
                ).toList(),
                this.stages.stream().map(ResearchStageSpec::toDefinition).toList(),
                this.requiredStages.stream().map(dep ->
                        new ResearchNodeDefinition.StageDependency(dep.nodeId().id(), dep.stageId().id())
                ).toList(),
                this.toast
        );
    }
}
