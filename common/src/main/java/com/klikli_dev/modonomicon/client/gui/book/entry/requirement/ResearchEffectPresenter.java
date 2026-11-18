/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Client side presentation of a {@link ResearchEffect} type. One presenter serves both uses of an effect:
 * as a reward row of the rewards category, and as the description of an action that applies it.
 * Mods register presenters for their own effect types via {@link ResearchEffectPresenterRegistry#register}.
 */
public interface ResearchEffectPresenter {

    /**
     * The row presenting the effect as a reward: its icons and a title starting with "Reward: ", plus optional details.
     * The category, the met state and the missing line are set by the caller. Reward rows are never clickable.
     */
    RequirementRow present(ResearchEffect effect, RequirementContext context);

    /**
     * The tooltip line describing what performing an action with this effect does, e.g. "Click to receive Research Note".
     */
    default Component line(ResearchEffect effect) {
        return Component.translatable(Gui.REQUIREMENTS_ACTION_PERFORM);
    }

    /**
     * The reason the effect cannot be applied right now, or null if it looks available. Server stays the authority.
     */
    @Nullable
    default Component unavailableReason(ResearchEffect effect, Player player) {
        return null;
    }
}
