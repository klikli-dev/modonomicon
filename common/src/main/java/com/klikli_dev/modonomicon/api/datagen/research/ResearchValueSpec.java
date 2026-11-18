/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Authoring-time value declaration that compiles into the canonical research value resource shape.
 *
 * @param ref the typed value ref being declared
 * @param toast optional toast display data
 * @param display optional display data for requirement blocks
 */
public record ResearchValueSpec(
        ResearchValueRef ref,
        Optional<ResearchToastDefinition> toast,
        Optional<ResearchDisplayModel> display
) {
    /**
     * Creates a value spec without toast or display data.
     */
    public static ResearchValueSpec of(ResearchValueRef ref) {
        return new ResearchValueSpec(ref, Optional.empty(), Optional.empty());
    }

    /**
     * Adds toast display data to this value spec.
     */
    public ResearchValueSpec toast(ResearchToastDefinition toast) {
        return new ResearchValueSpec(this.ref, Optional.of(toast), this.display);
    }

    /**
     * Merges display data into this value spec, see {@link ResearchDisplayModel#merge(ResearchDisplayModel)}.
     */
    public ResearchValueSpec withDisplay(ResearchDisplayModel display) {
        return this.display(current -> current.merge(display));
    }

    /**
     * Updates the display data of this value spec. The operator receives a copy of the existing display or an empty one.
     */
    public ResearchValueSpec display(UnaryOperator<ResearchDisplayModel> update) {
        return new ResearchValueSpec(this.ref, this.toast, Optional.of(update.apply(ResearchDisplayModel.copyOrCreate(this.display))));
    }

    /**
     * Compiles this authoring spec into the runtime/datapack value definition record.
     */
    public ResearchValueDefinition toDefinition() {
        return new ResearchValueDefinition(this.ref.id(), this.toast, this.display.map(ResearchDisplayModel::toResearchDisplay));
    }
}
