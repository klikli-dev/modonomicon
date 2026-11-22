/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

/**
 * Unified authoring-time research hook declaration.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id the canonical hook id
 * @param triggerType the trigger type for this hook
 * @param triggerTarget the typed target condition (e.g. Identifier for entry/advancement, ResearchItemMatcher for items)
 * @param factRef the fact granted when the trigger fires (null if incrementing a value)
 * @param valueRef the value incremented when the trigger fires (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 * @param consume the number of items of the used stack consumed when this hook changes research state (item_used only)
 */
public record ResearchHookSpec<TTarget>(
        Identifier id,
        TriggerType<TTarget, ?> triggerType,
        TTarget triggerTarget,
        @Nullable ResearchFactRef factRef,
        @Nullable ResearchValueRef valueRef,
        int increment,
        int consume
) {
    /**
     * Creates a hook spec for an item-based trigger type that grants a fact.
     */
    public static ResearchHookSpec<ResearchItemMatcher> grantFact(
            Identifier id, TriggerType<ResearchItemMatcher, ?> triggerType,
            ResearchItemMatcher targetItem, ResearchFactRef factRef
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, factRef, null, 1, 0);
    }

    /**
     * Creates a hook spec for an item-based trigger type that increments a value.
     */
    public static ResearchHookSpec<ResearchItemMatcher> incrementValue(
            Identifier id, TriggerType<ResearchItemMatcher, ?> triggerType,
            ResearchItemMatcher targetItem, ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, null, valueRef, increment, 0);
    }

    /**
     * Creates a hook spec for an Identifier-based trigger type that grants a fact.
     */
    public static ResearchHookSpec<Identifier> grantFact(
            Identifier id, TriggerType<Identifier, ?> triggerType,
            Identifier targetId, ResearchFactRef factRef
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetId, factRef, null, 1, 0);
    }

    /**
     * Creates a hook spec for an Identifier-based trigger type that increments a value.
     */
    public static ResearchHookSpec<Identifier> incrementValue(
            Identifier id, TriggerType<Identifier, ?> triggerType,
            Identifier targetId, ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetId, null, valueRef, increment, 0);
    }

    /**
     * Generic factory for arbitrary target types.
     */
    public static <TTarget> ResearchHookSpec<TTarget> of(
            Identifier id, TriggerType<TTarget, ?> triggerType,
            TTarget target, @Nullable ResearchFactRef factRef,
            @Nullable ResearchValueRef valueRef, int increment, int consume
    ) {
        return new ResearchHookSpec<>(id, triggerType, target, factRef, valueRef, increment, consume);
    }

    /**
     * Compiles this hook declaration into the canonical research hook record.
     */
    public ResearchHookDefinition<TTarget> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                this.triggerType,
                this.triggerTarget,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment,
                this.consume
        );
    }
}
