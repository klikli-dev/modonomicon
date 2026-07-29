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
 * @param triggerTarget the typed target condition (e.g. Identifier for entry/advancement, ItemStackTemplate for items)
 * @param factRef the fact granted when the trigger fires (null if incrementing a value)
 * @param valueRef the value incremented when the trigger fires (null if granting a fact)
 * @param increment the amount to add to the target value (default 1, ignored for fact hooks)
 */
public record ResearchHookSpec<TTarget>(
        Identifier id,
        TriggerType<TTarget, ?> triggerType,
        TTarget triggerTarget,
        @Nullable ResearchFactRef factRef,
        @Nullable ResearchValueRef valueRef,
        int increment
) {
    /**
     * Creates a hook spec for an item-based trigger type that grants a fact.
     */
    public static ResearchHookSpec<ItemStackTemplate> grantFact(
            Identifier id, TriggerType<ItemStackTemplate, ?> triggerType,
            ItemStackTemplate targetItem, ResearchFactRef factRef
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, factRef, null, 1);
    }

    /**
     * Creates a hook spec for an item-based trigger type that increments a value.
     */
    public static ResearchHookSpec<ItemStackTemplate> incrementValue(
            Identifier id, TriggerType<ItemStackTemplate, ?> triggerType,
            ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetItem, null, valueRef, increment);
    }

    /**
     * Creates a hook spec for an Identifier-based trigger type that grants a fact.
     */
    public static ResearchHookSpec<Identifier> grantFact(
            Identifier id, TriggerType<Identifier, ?> triggerType,
            Identifier targetId, ResearchFactRef factRef
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetId, factRef, null, 1);
    }

    /**
     * Creates a hook spec for an Identifier-based trigger type that increments a value.
     */
    public static ResearchHookSpec<Identifier> incrementValue(
            Identifier id, TriggerType<Identifier, ?> triggerType,
            Identifier targetId, ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, targetId, null, valueRef, increment);
    }

    /**
     * Generic factory for arbitrary target types.
     */
    public static <TTarget> ResearchHookSpec<TTarget> of(
            Identifier id, TriggerType<TTarget, ?> triggerType,
            TTarget target, @Nullable ResearchFactRef factRef,
            @Nullable ResearchValueRef valueRef, int increment
    ) {
        return new ResearchHookSpec<>(id, triggerType, target, factRef, valueRef, increment);
    }

    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition<TTarget> toDefinition() {
        return new ResearchHookDefinition<>(
                this.id,
                this.triggerType,
                this.triggerTarget,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
