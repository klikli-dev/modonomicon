/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Authoring-time research ingress declaration for the {@code item_acquired} trigger family.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id the canonical hook id
 * @param targetItem the item stack template that triggers research progression
 * @param factRef the fact granted when that ingress event occurs (null if incrementing a value)
 * @param valueRef the value incremented when that ingress event occurs (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 */
public record ItemAcquiredHookSpec(
        Identifier id,
        ItemStackTemplate targetItem,
        ResearchFactRef factRef,
        ResearchValueRef valueRef,
        int increment
) {
    /**
     * Creates a hook spec that grants a fact.
     */
    public static ItemAcquiredHookSpec grantFact(Identifier id, ItemStackTemplate targetItem, ResearchFactRef factRef) {
        return new ItemAcquiredHookSpec(id, targetItem, factRef, null, 1);
    }

    /**
     * Creates a hook spec that increments a value.
     */
    public static ItemAcquiredHookSpec incrementValue(Identifier id, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        return new ItemAcquiredHookSpec(id, targetItem, null, valueRef, increment);
    }

    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition<ItemStackTemplate> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                TriggerTypeRegistry.ITEM_ACQUIRED,
                this.targetItem,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
