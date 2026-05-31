/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;

import java.util.List;

/**
 * Authoring-time node declaration that compiles into the canonical research node resource shape.
 *
 * @param ref the typed node ref being declared
 * @param requiredFacts the fact refs that currently act as this node's unlock requirements
 */
public record ResearchNodeSpec(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts) {
    /**
     * Compiles this authoring spec into the runtime/datapack node definition record.
     */
    public ResearchNodeDefinition toDefinition() {
        return new ResearchNodeDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList()
        );
    }
}
