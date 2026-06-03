/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;

import java.util.Optional;

/**
 * Authoring-time fact declaration that compiles into the canonical research fact resource shape.
 *
 * @param ref the typed fact ref being declared
 * @param toast optional toast display data
 */
public record ResearchFactSpec(
        ResearchFactRef ref,
        Optional<ResearchToastDefinition> toast
) {
    /**
     * Creates a fact spec without toast data.
     */
    public static ResearchFactSpec of(ResearchFactRef ref) {
        return new ResearchFactSpec(ref, Optional.empty());
    }

    /**
     * Adds toast display data to this fact spec.
     */
    public ResearchFactSpec toast(ResearchToastDefinition toast) {
        return new ResearchFactSpec(this.ref, Optional.of(toast));
    }

    /**
     * Compiles this authoring spec into the runtime/datapack fact definition record.
     */
    public ResearchFactDefinition toDefinition() {
        return new ResearchFactDefinition(this.ref.id(), this.toast);
    }
}
