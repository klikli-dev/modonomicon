/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;

/**
 * Trigger-bound research hook authoring step.
 *
 * Binds one trigger type and one authored target (e.g. an entry id, advancement id or item) and
 * authors hooks that either grant facts or increment values when that trigger fires for that target.
 *
 * Hook ids derived by the shortcut methods ({@link #declareFact}, {@link #grant}, {@link #declareValue},
 * {@link #increment}) are generated via {@link ResearchDataBuilder#derivedHookPath}: the ref path with
 * {@code _hook} appended, prefixed with the ref namespace if it differs from the builder namespace, and
 * suffixed with {@code _2}, {@code _3}, ... if the same ref is already hooked up by another trigger.
 *
 * @param <TTarget> the authored target type of the bound trigger type
 */
public class ResearchHookBuilder<TTarget> {
    protected final ResearchDataBuilder research;
    protected final TriggerType<TTarget, ?> triggerType;
    protected final TTarget target;

    /**
     * Creates a hook step bound to one specific trigger type and target.
     */
    public ResearchHookBuilder(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
        this.research = research;
        this.triggerType = triggerType;
        this.target = target;
    }

    /**
     * Declares a new research fact at the given path and authors a hook that grants it.
     * The hook id is derived from the fact id, see {@link ResearchDataBuilder#derivedHookPath}.
     */
    public ResearchFactRef declareFact(String factPath) {
        var factRef = this.research.fact(factPath);
        this.grant(factRef);
        return factRef;
    }

    /**
     * Declares the given research fact (with exactly its id, in any namespace) and authors a hook that grants it.
     * The hook id is derived from the fact id in the namespace of this builder, see {@link ResearchDataBuilder#derivedHookPath}.
     */
    public ResearchFactRef declareFact(ResearchFactRef factRef) {
        this.research.fact(factRef);
        this.grant(factRef);
        return factRef;
    }

    /**
     * Authors a hook that grants the given already-declared fact.
     * The hook id is derived from the fact id in the namespace of this builder, see {@link ResearchDataBuilder#derivedHookPath}.
     * The same fact may be granted from multiple triggers.
     *
     * @throws IllegalArgumentException if the fact was not declared in this builder
     */
    public void grant(ResearchFactRef factRef) {
        this.research.requireFact(factRef);
        this.grantFact(this.research.derivedHookPath(factRef.id()), factRef);
    }

    /**
     * Authors an explicit hook with the given path that grants the given already-declared fact.
     *
     * @throws IllegalArgumentException if a hook with the same id was already declared
     */
    public void grantFact(String hookPath, ResearchFactRef factRef) {
        this.research.grantFact(hookPath, this.triggerType, this.target, factRef, this.consumeCount());
    }

    /**
     * Number of items of the used stack that the hooks of this hook consume when they change research state.
     * Only item_used hook consumes items, all other hook consume nothing.
     */
    protected int consumeCount() {
        return 0;
    }

    /**
     * Declares a new research value at the given path and authors a hook that increments it.
     * The hook id is derived from the value id, see {@link ResearchDataBuilder#derivedHookPath}.
     */
    public ResearchValueRef declareValue(String valuePath, int increment) {
        var valueRef = this.research.value(valuePath);
        this.increment(valueRef, increment);
        return valueRef;
    }

    /**
     * Declares the given research value (with exactly its id, in any namespace) and authors a hook that increments it.
     * The hook id is derived from the value id in the namespace of this builder, see {@link ResearchDataBuilder#derivedHookPath}.
     */
    public ResearchValueRef declareValue(ResearchValueRef valueRef, int increment) {
        this.research.value(valueRef);
        this.increment(valueRef, increment);
        return valueRef;
    }

    /**
     * Authors a hook that increments the given already-declared value.
     * The hook id is derived from the value id in the namespace of this builder, see {@link ResearchDataBuilder#derivedHookPath}.
     * The same value may be incremented from multiple triggers.
     *
     * @throws IllegalArgumentException if the value was not declared in this builder
     */
    public void increment(ResearchValueRef valueRef, int amount) {
        this.research.requireValue(valueRef);
        this.incrementValue(this.research.derivedHookPath(valueRef.id()), valueRef, amount);
    }

    /**
     * Authors an explicit hook with the given path that increments the given already-declared value.
     *
     * @throws IllegalArgumentException if a hook with the same id was already declared
     */
    public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
        this.research.incrementValue(hookPath, this.triggerType, this.target, valueRef, increment, this.consumeCount());
    }
}
