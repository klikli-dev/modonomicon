/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchStageCompletion;
import com.klikli_dev.modonomicon.research.data.ResearchStageDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Authoring-time stage declaration that compiles into the canonical stage definition.
 *
 * @param ref the typed stage ref being declared
 * @param requiredFacts the fact refs required to advance past this stage
 * @param requiredValues the value requirements required to advance past this stage
 * @param toast optional toast display data for stage completion
 * @param display optional display data of the stage, e.g. its title in requirement blocks
 * @param completion how the stage completes, automatic by default, see {@link #withManual()}
 * @param effects effects granted once when the stage completes, see {@link #withEffect(ResearchEffectModel)}
 */
public record ResearchStageSpec(
        ResearchStageRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ResearchNodeSpec.ValueRequirement> requiredValues,
        Optional<ResearchToastDefinition> toast,
        Optional<ResearchDisplayModel> display,
        ResearchStageCompletion completion,
        List<ResearchEffectModel> effects
) {
    public static ResearchStageSpec factsOnly(ResearchStageRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchStageSpec(ref, requiredFacts, List.of(), Optional.empty(), Optional.empty(), ResearchStageCompletion.AUTO, List.of());
    }

    public static ResearchStageSpec valuesOnly(ResearchStageRef ref, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        return new ResearchStageSpec(ref, List.of(), requiredValues, Optional.empty(), Optional.empty(), ResearchStageCompletion.AUTO, List.of());
    }

    public static ResearchStageSpec none(ResearchStageRef ref) {
        return new ResearchStageSpec(ref, List.of(), List.of(), Optional.empty(), Optional.empty(), ResearchStageCompletion.AUTO, List.of());
    }

    /**
     * Adds toast display data to this stage spec.
     */
    public ResearchStageSpec withToast(ResearchToastDefinition toast) {
        return new ResearchStageSpec(this.ref, this.requiredFacts, this.requiredValues, Optional.of(toast), this.display, this.completion, this.effects);
    }

    /**
     * The stage is completed by hand: once its requirements are met it waits for the player to complete it, see {@code ResearchStages.completeManual}.
     */
    public ResearchStageSpec withManual() {
        return this.withManual(ResearchCostModel.create());
    }

    /**
     * The stage is completed by hand, consuming the cost on completion. The cost is consumed even if it has no items.
     */
    public ResearchStageSpec withManual(ResearchCostModel cost) {
        return this.withCompletion(ResearchStageCompletion.manual(cost.toResearchCost()));
    }

    /**
     * Sets the completion of the stage, e.g. a manual completion with a cost, see {@link #withManual(ResearchCostModel)}.
     */
    public ResearchStageSpec withCompletion(ResearchStageCompletion completion) {
        return new ResearchStageSpec(this.ref, this.requiredFacts, this.requiredValues, this.toast, this.display, completion, this.effects);
    }

    /**
     * Adds an effect, granted once when the stage completes.
     */
    public ResearchStageSpec withEffect(ResearchEffectModel effect) {
        var effects = new ArrayList<>(this.effects);
        effects.add(effect);
        return new ResearchStageSpec(this.ref, this.requiredFacts, this.requiredValues, this.toast, this.display, this.completion, List.copyOf(effects));
    }

    /**
     * Merges display data into this stage spec, see {@link ResearchDisplayModel#merge(ResearchDisplayModel)}.
     */
    public ResearchStageSpec withDisplay(ResearchDisplayModel display) {
        return this.withDisplay(current -> current.merge(display));
    }

    /**
     * Updates the display data of this stage spec. The operator receives a copy of the existing display or an empty one.
     */
    public ResearchStageSpec withDisplay(UnaryOperator<ResearchDisplayModel> update) {
        return new ResearchStageSpec(this.ref, this.requiredFacts, this.requiredValues, this.toast,
                Optional.of(update.apply(ResearchDisplayModel.copyOrCreate(this.display))), this.completion, this.effects);
    }

    public ResearchStageDefinition toDefinition() {
        return new ResearchStageDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList(),
                this.requiredValues.stream().map(req ->
                        new ResearchNodeDefinition.ValueRequirement(req.valueRef().id(), req.threshold())
                ).toList(),
                this.toast,
                this.display.map(ResearchDisplayModel::toResearchDisplay),
                this.completion,
                this.effects.stream().map(ResearchEffectModel::toResearchEffect).toList()
        );
    }
}
