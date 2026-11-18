/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;

/**
 * Trigger-bound research ingress authoring step.
 *
 * Binds one trigger type and one authored target (e.g. an entry id, advancement id or item) and
 * authors hooks that either grant facts or increment values when that trigger fires for that target.
 *
 * @param <TTarget> the authored target type of the bound trigger type
 */
public class ResearchIngress<TTarget> {
    protected final ResearchDataBuilder research;
    protected final TriggerType<TTarget, ?> triggerType;
    protected final TTarget target;

    /**
     * Creates an ingress step bound to one specific trigger type and target.
     */
    public ResearchIngress(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
        this.research = research;
        this.triggerType = triggerType;
        this.target = target;
    }

    /**
     * Declares a new research fact at the given path and authors a hook that grants it.
     * The hook id is the fact path with {@code _hook} appended.
     */
    public ResearchFactRef declareFact(String factPath) {
        var factRef = this.research.fact(factPath);
        this.grantFact(factPath + "_hook", factRef);
        return factRef;
    }

    /**
     * Declares the given research fact (with exactly its id, in any namespace) and authors a hook that grants it.
     * The hook id is the fact path with {@code _hook} appended, in the namespace of this builder.
     */
    public ResearchFactRef declareFact(ResearchFactRef factRef) {
        this.research.fact(factRef);
        this.grant(factRef);
        return factRef;
    }

    /**
     * Authors a hook that grants the given already-declared fact.
     * The hook id is the fact path with {@code _hook} appended, in the namespace of this builder.
     *
     * @throws IllegalArgumentException if the fact was not declared in this builder
     */
    public void grant(ResearchFactRef factRef) {
        this.research.requireFact(factRef);
        this.grantFact(factRef.id().getPath() + "_hook", factRef);
    }

    /**
     * Authors an explicit ingress hook with the given path that grants the given already-declared fact.
     */
    public void grantFact(String hookPath, ResearchFactRef factRef) {
        this.research.grantFact(hookPath, this.triggerType, this.target, factRef);
    }

    /**
     * Declares a new research value at the given path and authors a hook that increments it.
     * The hook id is the value path with {@code _hook} appended.
     */
    public ResearchValueRef declareValue(String valuePath, int increment) {
        var valueRef = this.research.value(valuePath);
        this.incrementValue(valuePath + "_hook", valueRef, increment);
        return valueRef;
    }

    /**
     * Declares the given research value (with exactly its id, in any namespace) and authors a hook that increments it.
     * The hook id is the value path with {@code _hook} appended, in the namespace of this builder.
     */
    public ResearchValueRef declareValue(ResearchValueRef valueRef, int increment) {
        this.research.value(valueRef);
        this.increment(valueRef, increment);
        return valueRef;
    }

    /**
     * Authors a hook that increments the given already-declared value.
     * The hook id is the value path with {@code _hook} appended, in the namespace of this builder.
     *
     * @throws IllegalArgumentException if the value was not declared in this builder
     */
    public void increment(ResearchValueRef valueRef, int amount) {
        this.research.requireValue(valueRef);
        this.incrementValue(valueRef.id().getPath() + "_hook", valueRef, amount);
    }

    /**
     * Authors an explicit ingress hook with the given path that increments the given already-declared value.
     */
    public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
        this.research.incrementValue(hookPath, this.triggerType, this.target, valueRef, increment);
    }
}
