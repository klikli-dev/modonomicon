/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

/**
 * Unified authoring-time research ingress declaration.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id the canonical hook id
 * @param triggerType the trigger type for this hook
 * @param triggerTargetId the target id (entry id, item id, advancement id, etc.)
 * @param factRef the fact granted when the trigger fires (null if incrementing a value)
 * @param valueRef the value incremented when the trigger fires (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 * @param targetItem optional item stack template for component matching (null for non-item hooks)
 * @param matchComponents whether to check components when targetItem is set (default false)
 */
public record ResearchHookSpec(
        Identifier id,
        TriggerType triggerType,
        Identifier triggerTargetId,
        @Nullable ResearchFactRef factRef,
        @Nullable ResearchValueRef valueRef,
        int increment,
        @Nullable ItemStackTemplate targetItem,
        boolean matchComponents
) {
    /**
     * Creates a hook spec for an item-based trigger type that grants a fact.
     */
    public static ResearchHookSpec grantFact(Identifier id, TriggerType triggerType, ItemStackTemplate targetItem) {
        var itemId = targetItem.item().unwrapKey().map(net.minecraft.resources.ResourceKey::identifier).orElseThrow();
        return new ResearchHookSpec(id, triggerType, itemId, null, null, 1, targetItem, false);
    }

    /**
     * Creates a hook spec for an item-based trigger type that grants a fact with component matching.
     */
    public static ResearchHookSpec grantFact(Identifier id, TriggerType triggerType, ItemStackTemplate targetItem, boolean matchComponents) {
        var itemId = targetItem.item().unwrapKey().map(net.minecraft.resources.ResourceKey::identifier).orElseThrow();
        return new ResearchHookSpec(id, triggerType, itemId, null, null, 1, targetItem, matchComponents);
    }

    /**
     * Creates a hook spec for an item-based trigger type that increments a value.
     */
    public static ResearchHookSpec incrementValue(Identifier id, TriggerType triggerType, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        var itemId = targetItem.item().unwrapKey().map(net.minecraft.resources.ResourceKey::identifier).orElseThrow();
        return new ResearchHookSpec(id, triggerType, itemId, null, valueRef, increment, targetItem, false);
    }

    /**
     * Creates a hook spec for an item-based trigger type that increments a value with component matching.
     */
    public static ResearchHookSpec incrementValue(Identifier id, TriggerType triggerType, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment, boolean matchComponents) {
        var itemId = targetItem.item().unwrapKey().map(net.minecraft.resources.ResourceKey::identifier).orElseThrow();
        return new ResearchHookSpec(id, triggerType, itemId, null, valueRef, increment, targetItem, matchComponents);
    }

    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition toDefinition() {
        return new ResearchHookDefinition(
                this.id,
                this.triggerType,
                this.triggerTargetId,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment,
                this.targetItem,
                this.matchComponents
        );
    }
}
