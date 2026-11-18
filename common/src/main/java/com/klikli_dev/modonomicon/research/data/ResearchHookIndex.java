/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hooks of a single trigger type, pre-grouped by the handler's index key so that an event only needs to check the
 * hooks that can possibly match it.
 *
 * <p>Hooks without an index key (e.g. item tag matchers) cannot be grouped and are part of every candidate list.
 * Candidate lists keep the authored hook order, so applying them yields the same effects as scanning all hooks.
 *
 * @param <TTarget> the authored target type of the trigger type
 */
public final class ResearchHookIndex<TTarget> {

    private static final ResearchHookIndex<?> EMPTY = new ResearchHookIndex<>(List.of(), List.of(), Map.of());

    private final List<ResearchHookDefinition<TTarget>> allHooks;
    private final List<ResearchHookDefinition<TTarget>> unkeyedHooks;
    private final Map<Object, List<ResearchHookDefinition<TTarget>>> hooksByKey;

    private ResearchHookIndex(
            List<ResearchHookDefinition<TTarget>> allHooks,
            List<ResearchHookDefinition<TTarget>> unkeyedHooks,
            Map<Object, List<ResearchHookDefinition<TTarget>>> hooksByKey
    ) {
        this.allHooks = allHooks;
        this.unkeyedHooks = unkeyedHooks;
        this.hooksByKey = hooksByKey;
    }

    @SuppressWarnings("unchecked")
    public static <TTarget> ResearchHookIndex<TTarget> empty() {
        return (ResearchHookIndex<TTarget>) EMPTY;
    }

    /**
     * Builds the index for the given hooks, which must all belong to the given trigger type.
     */
    @SuppressWarnings("unchecked")
    public static <TTarget, TContext extends TriggerContext> ResearchHookIndex<TTarget> of(
            TriggerType<TTarget, TContext> type,
            List<? extends ResearchHookDefinition<?>> hooks
    ) {
        var handler = type.handler();
        List<ResearchHookDefinition<TTarget>> allHooks = new ArrayList<>(hooks.size());
        List<Object> targetKeys = new ArrayList<>(hooks.size());
        for (var hook : hooks) {
            var typed = (ResearchHookDefinition<TTarget>) hook;
            allHooks.add(typed);
            targetKeys.add(handler.indexKey(typed.triggerTarget()));
        }

        List<ResearchHookDefinition<TTarget>> unkeyedHooks = new ArrayList<>();
        for (int i = 0; i < allHooks.size(); i++) {
            if (targetKeys.get(i) == null) {
                unkeyedHooks.add(allHooks.get(i));
            }
        }

        // Collect, per key, the positions of the hooks with that key in one pass.
        var indicesByKey = new HashMap<Object, List<Integer>>();
        for (int i = 0; i < targetKeys.size(); i++) {
            var key = targetKeys.get(i);
            if (key != null) {
                indicesByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }
        }

        // Merge each key's hooks with the shared unkeyed hooks, preserving authored order.
        var unkeyedIndices = new ArrayList<Integer>(unkeyedHooks.size());
        for (int i = 0; i < targetKeys.size(); i++) {
            if (targetKeys.get(i) == null) {
                unkeyedIndices.add(i);
            }
        }

        var hooksByKey = new HashMap<Object, List<ResearchHookDefinition<TTarget>>>(indicesByKey.size());
        for (var entry : indicesByKey.entrySet()) {
            var keyedIndices = entry.getValue();
            List<ResearchHookDefinition<TTarget>> candidates = new ArrayList<>(keyedIndices.size() + unkeyedIndices.size());
            int k = 0;
            int u = 0;
            while (k < keyedIndices.size() || u < unkeyedIndices.size()) {
                if (u >= unkeyedIndices.size() || (k < keyedIndices.size() && keyedIndices.get(k) < unkeyedIndices.get(u))) {
                    candidates.add(allHooks.get(keyedIndices.get(k++)));
                } else {
                    candidates.add(allHooks.get(unkeyedIndices.get(u++)));
                }
            }
            hooksByKey.put(entry.getKey(), List.copyOf(candidates));
        }

        return new ResearchHookIndex<>(List.copyOf(allHooks), List.copyOf(unkeyedHooks), Map.copyOf(hooksByKey));
    }

    /**
     * All hooks of this trigger type in authored order.
     */
    public List<ResearchHookDefinition<TTarget>> all() {
        return this.allHooks;
    }

    /**
     * Hooks that may match an event with the given index key, in authored order.
     * Falls back to the unkeyed hooks if no hook targets this key.
     */
    public List<ResearchHookDefinition<TTarget>> candidatesFor(@Nullable Object key) {
        if (key == null) {
            return this.allHooks;
        }
        return this.hooksByKey.getOrDefault(key, this.unkeyedHooks);
    }
}
