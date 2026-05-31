/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;

/**
 * Authoring-time fact declaration that compiles into the canonical research fact resource shape.
 *
 * @param ref the typed fact ref being declared
 */
public record ResearchFactSpec(ResearchFactRef ref) {
    /**
     * Compiles this authoring spec into the runtime/datapack fact definition record.
     */
    public ResearchFactDefinition toDefinition() {
        return new ResearchFactDefinition(this.ref.id());
    }
}
