/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;

import java.util.Optional;

/**
 * Authoring-time value declaration that compiles into the canonical research value resource shape.
 *
 * @param ref the typed value ref being declared
 * @param toast optional toast display data
 */
public record ResearchValueSpec(
        ResearchValueRef ref,
        Optional<ResearchToastDefinition> toast
) {
    /**
     * Creates a value spec without toast data.
     */
    public static ResearchValueSpec of(ResearchValueRef ref) {
        return new ResearchValueSpec(ref, Optional.empty());
    }

    /**
     * Adds toast display data to this value spec.
     */
    public ResearchValueSpec toast(ResearchToastDefinition toast) {
        return new ResearchValueSpec(this.ref, Optional.of(toast));
    }

    /**
     * Compiles this authoring spec into the runtime/datapack value definition record.
     */
    public ResearchValueDefinition toDefinition() {
        return new ResearchValueDefinition(this.ref.id(), this.toast);
    }
}
