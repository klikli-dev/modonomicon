/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import net.minecraft.server.level.ServerPlayer;

/**
 * An effect applied to a player's research state or inventory, e.g. granting a fact or giving an item.
 * Effects are the building blocks of stage effects ({@code ResearchStageDefinition#effects}) and of actions ({@code ResearchAction#effects}).
 * Mods add effect types via {@link ResearchEffectTypeRegistry#register}.
 * <p>
 * Effects only run on the server.
 */
public interface ResearchEffect {

    ResearchEffectType<?> type();

    /**
     * Applies the effect to the player. Returns true if the effect changed something: research state, inventory or experience.
     * Actions use the result, an action that changes nothing is not performed and costs nothing.
     * Availability is checked by the caller before, see {@link #isAvailable}.
     */
    boolean apply(ServerPlayer player, ResearchEffectContext context);

    /**
     * Whether the effect can be applied right now, e.g. the player does not already hold the item. Effects without such
     * checks are always available. An action is available if all of its effects are.
     */
    default boolean isAvailable(ServerPlayer player, ResearchEffectContext context) {
        return true;
    }
}
