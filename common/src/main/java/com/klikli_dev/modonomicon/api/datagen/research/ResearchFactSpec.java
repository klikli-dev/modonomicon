/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Authoring-time fact declaration that compiles into the canonical research fact resource shape.
 *
 * @param ref the typed fact ref being declared
 * @param toast optional toast display data
 * @param display optional display data for requirement blocks
 */
public record ResearchFactSpec(
        ResearchFactRef ref,
        Optional<ResearchToastDefinition> toast,
        Optional<ResearchDisplayModel> display
) {
    /**
     * Creates a fact spec without toast or display data.
     */
    public static ResearchFactSpec of(ResearchFactRef ref) {
        return new ResearchFactSpec(ref, Optional.empty(), Optional.empty());
    }

    /**
     * Adds toast display data to this fact spec.
     */
    public ResearchFactSpec withToast(ResearchToastDefinition toast) {
        return new ResearchFactSpec(this.ref, Optional.of(toast), this.display);
    }

    /**
     * @deprecated use {@link #withToast(ResearchToastDefinition)}.
     */
    @Deprecated(forRemoval = true)
    public ResearchFactSpec toast(ResearchToastDefinition toast) {
        return this.withToast(toast);
    }

    /**
     * Merges display data into this fact spec, see {@link ResearchDisplayModel#merge(ResearchDisplayModel)}.
     */
    public ResearchFactSpec withDisplay(ResearchDisplayModel display) {
        return this.withDisplay(current -> current.merge(display));
    }

    /**
     * Updates the display data of this fact spec. The operator receives a copy of the existing display or an empty one.
     */
    public ResearchFactSpec withDisplay(UnaryOperator<ResearchDisplayModel> update) {
        return new ResearchFactSpec(this.ref, this.toast, Optional.of(update.apply(ResearchDisplayModel.copyOrCreate(this.display))));
    }

    /**
     * Compiles this authoring spec into the runtime/datapack fact definition record.
     */
    public ResearchFactDefinition toDefinition() {
        return new ResearchFactDefinition(this.ref.id(), this.toast, this.display.map(ResearchDisplayModel::toResearchDisplay));
    }
}
