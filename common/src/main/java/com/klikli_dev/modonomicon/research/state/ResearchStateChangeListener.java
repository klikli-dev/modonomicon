/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import net.minecraft.server.level.ServerPlayer;

/**
 * Observes a batch of research mutations applied to a player by the hook service.
 *
 * <p>Listeners are called around each batch in this order: {@link #beforeChange(ServerPlayer)} runs before any mutation,
 * then the mutations and toast collection happen, the collected toasts are sent, and finally the returned
 * {@link Completion} is invoked with whether any research state changed. This lets other domains (e.g. books) take a
 * snapshot before and compare it after without research needing to know about them.
 */
@FunctionalInterface
public interface ResearchStateChangeListener {

    /**
     * Called before the mutations of a batch.
     *
     * @param player the player whose research state is about to change
     * @return the callback that is invoked after the batch has finished
     */
    Completion beforeChange(ServerPlayer player);

    /**
     * Callback returned by {@link #beforeChange(ServerPlayer)}, carrying whatever snapshot the listener took.
     */
    @FunctionalInterface
    interface Completion {

        /**
         * @param changed true if the batch changed any research state
         */
        void afterChange(boolean changed);
    }
}
