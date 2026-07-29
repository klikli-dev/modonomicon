/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extension point for research hook trigger types.
 * Each trigger type registers a handler that knows how to resolve
 * hooks for a specific event and optionally replay all hooks of that type.
 *
 * @param <TTarget> the authored target type stored in hook data (e.g. Identifier, ItemStackTemplate)
 * @param <TContext> the runtime context type carrying event data (e.g. EntryViewedContext, ItemCraftedContext)
 */
public interface TriggerHandler<TTarget, TContext extends TriggerContext> {

    /**
     * Resolve hooks matching a specific event context.
     * Called by the hook service when a concrete event fires.
     *
     * @param type the trigger type being resolved
     * @param player the player who triggered the event
     * @param context the runtime event context
     * @return the list of hooks whose target condition is met
     */
    List<ResearchHookDefinition<TTarget>> resolve(
            TriggerType<TTarget, TContext> type,
            ServerPlayer player,
            TContext context
    );

    /**
     * Check whether a single authored target matches a given runtime context.
     *
     * @param target the authored target from hook data
     * @param context the runtime event context
     * @return true if the target matches the context
     */
    boolean matches(TTarget target, TContext context);

    /**
     * Return a stable index key for this target, or null if no fast-path
     * index is available.
     *
     * @param target the authored target from hook data
     * @return an index key for fast lookup, or null
     */
    default @Nullable Object indexKey(TTarget target) {
        return null;
    }

    /**
     * Return a stable index key for this context, or null if no fast-path
     * index is available.
     *
     * @param context the runtime event context
     * @return an index key for fast lookup, or null
     */
    default @Nullable Object indexKey(TContext context) {
        return null;
    }

    /**
     * Enumerate ALL hooks of this trigger type and fire those whose
     * target condition is met for the given player. Used for replay
     * on login / research reset.
     *
     * The default returns false (no-op) — trigger types that don't
     * need replay get this for free.
     *
     * @param player the player to replay for
     * @param stateManager the state manager to grant facts/increment values
     * @return true if any research state changed
     */
    default boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        return false;
    }
}
