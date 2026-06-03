/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;

/**
 * Authoring-time value declaration that compiles into the canonical research value resource shape.
 *
 * @param ref the typed value ref being declared
 */
public record ResearchValueSpec(ResearchValueRef ref) {
    /**
     * Compiles this authoring spec into the runtime/datapack value definition record.
     */
    public ResearchValueDefinition toDefinition() {
        return new ResearchValueDefinition(this.ref.id());
    }
}
