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
 * Authoring-time research ingress declaration for the {@code entry_viewed_once} trigger family.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id the canonical hook id
 * @param entryId the book entry whose first view acts as ingress into research
 * @param factRef the fact granted when that ingress event occurs (null if incrementing a value)
 * @param valueRef the value incremented when that ingress event occurs (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 */
public record EntryViewedOnceHookSpec(
        Identifier id,
        Identifier entryId,
        ResearchFactRef factRef,
        ResearchValueRef valueRef,
        int increment
) {
    /**
     * Creates a hook spec that grants a fact.
     */
    public static EntryViewedOnceHookSpec grantFact(Identifier id, Identifier entryId, ResearchFactRef factRef) {
        return new EntryViewedOnceHookSpec(id, entryId, factRef, null, 1);
    }

    /**
     * Creates a hook spec that increments a value.
     */
    public static EntryViewedOnceHookSpec incrementValue(Identifier id, Identifier entryId, ResearchValueRef valueRef, int increment) {
        return new EntryViewedOnceHookSpec(id, entryId, null, valueRef, increment);
    }

    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition toDefinition() {
        return new ResearchHookDefinition(
                this.id,
                TriggerTypeRegistry.ENTRY_VIEWED_ONCE,
                this.entryId,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
