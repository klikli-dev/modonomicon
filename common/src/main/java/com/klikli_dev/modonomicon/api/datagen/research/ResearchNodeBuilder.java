/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for authoring research nodes with stages, stage dependencies, and other options.
 *
 * <p>Use {@link ResearchDataBuilder#nodeBuilder(ResearchNodeRef)} to create an instance, then chain
 * calls to declare requirements and stages, and finally call {@link #build()} to register the node.
 *
 * <p>Example usage:
 * <pre>{@code
 * this.nodeBuilder(STAGES_DEMO)
 *     .withFact(conditionRootViewed)
 *     .withStage(ResearchStageSpec.valuesOnly(stage1, List.of(...)))
 *     .withStage(ResearchStageSpec.valuesOnly(stage2, List.of(...)))
 *     .build();
 * }</pre>
 */
public final class ResearchNodeBuilder {
    private final ResearchDataBuilder research;
    private final ResearchNodeRef ref;
    private final List<ResearchFactRef> requiredFacts = new ArrayList<>();
    private final List<ResearchNodeSpec.ValueRequirement> requiredValues = new ArrayList<>();
    private final List<ResearchStageSpec> stages = new ArrayList<>();
    private final List<ResearchNodeSpec.StageDependencySpec> stageDependencies = new ArrayList<>();
    private ResearchToastDefinition toast;

    ResearchNodeBuilder(ResearchDataBuilder research, ResearchNodeRef ref) {
        this.research = research;
        this.ref = ref;
    }

    /**
     * Adds a fact requirement to this node.
     */
    public ResearchNodeBuilder withFact(ResearchFactRef factRef) {
        this.requiredFacts.add(factRef);
        return this;
    }

    /**
     * Adds multiple fact requirements to this node.
     */
    public ResearchNodeBuilder withFacts(List<ResearchFactRef> factRefs) {
        this.requiredFacts.addAll(factRefs);
        return this;
    }

    /**
     * Adds a value requirement to this node.
     */
    public ResearchNodeBuilder withValue(ResearchNodeSpec.ValueRequirement valueRequirement) {
        this.requiredValues.add(valueRequirement);
        return this;
    }

    /**
     * Adds multiple value requirements to this node.
     */
    public ResearchNodeBuilder withValues(List<ResearchNodeSpec.ValueRequirement> valueRequirements) {
        this.requiredValues.addAll(valueRequirements);
        return this;
    }

    /**
     * Adds a stage to this node. Stages are ordered and define the node's intrinsic progression.
     */
    public ResearchNodeBuilder withStage(ResearchStageSpec stage) {
        this.stages.add(stage);
        return this;
    }

    /**
     * Adds multiple stages to this node.
     */
    public ResearchNodeBuilder withStages(List<ResearchStageSpec> stages) {
        this.stages.addAll(stages);
        return this;
    }

    /**
     * Adds a dependency on another node reaching a specific stage.
     */
    public ResearchNodeBuilder withStageDependency(ResearchNodeRef requiredNode, ResearchStageRef requiredStage) {
        this.stageDependencies.add(ResearchNodeSpec.StageDependencySpec.of(requiredNode, requiredStage));
        return this;
    }

    /**
     * Adds a stage dependency spec directly.
     */
    public ResearchNodeBuilder withStageDependency(ResearchNodeSpec.StageDependencySpec dependency) {
        this.stageDependencies.add(dependency);
        return this;
    }

    /**
     * Adds multiple stage dependencies to this node.
     */
    public ResearchNodeBuilder withStageDependencies(List<ResearchNodeSpec.StageDependencySpec> dependencies) {
        this.stageDependencies.addAll(dependencies);
        return this;
    }

    /**
     * Sets the toast display data for full node completion.
     */
    public ResearchNodeBuilder withToast(ResearchToastDefinition toast) {
        this.toast = toast;
        return this;
    }

    /**
     * Builds and registers the node, returning the node ref.
     */
    public ResearchNodeRef build() {
        this.research.node(
                this.ref,
                List.copyOf(this.requiredFacts),
                List.copyOf(this.requiredValues),
                List.copyOf(this.stages),
                List.copyOf(this.stageDependencies),
                this.toast
        );
        return this.ref;
    }
}
