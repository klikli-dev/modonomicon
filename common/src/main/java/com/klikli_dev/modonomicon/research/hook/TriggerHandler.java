/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Extension point for research hook trigger types.
 * Each trigger type registers a handler that knows how to resolve hooks
 * for a specific event and optionally replay all hooks of that type.
 */
public interface TriggerHandler {

    /**
     * Resolve hooks matching a specific event target.
     * Called by the hook service when a concrete event fires.
     *
     * @param player the player who triggered the event
     * @param targetId the target id (entry id, item id, advancement id, etc.)
     * @return the list of hooks whose target condition is met
     */
    List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier targetId);

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
