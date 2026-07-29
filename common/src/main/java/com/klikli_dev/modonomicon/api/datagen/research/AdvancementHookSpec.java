/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;

/**
 * Authoring-time research ingress declaration for advancement completion.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id the canonical hook id
 * @param advancementId the advancement whose completion acts as ingress into research
 * @param factRef the fact granted when that ingress event occurs (null if incrementing a value)
 * @param valueRef the value incremented when that ingress event occurs (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 */
public record AdvancementHookSpec(
        Identifier id,
        Identifier advancementId,
        ResearchFactRef factRef,
        ResearchValueRef valueRef,
        int increment
) {
    /**
     * Creates a hook spec that grants a fact.
     */
    public static AdvancementHookSpec grantFact(Identifier id, Identifier advancementId, ResearchFactRef factRef) {
        return new AdvancementHookSpec(id, advancementId, factRef, null, 1);
    }

    /**
     * Creates a hook spec that increments a value.
     */
    public static AdvancementHookSpec incrementValue(Identifier id, Identifier advancementId, ResearchValueRef valueRef, int increment) {
        return new AdvancementHookSpec(id, advancementId, null, valueRef, increment);
    }

    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition<Identifier> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                TriggerTypeRegistry.ADVANCEMENT,
                this.advancementId,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
