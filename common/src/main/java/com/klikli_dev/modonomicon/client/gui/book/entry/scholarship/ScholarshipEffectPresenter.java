/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Client side presentation of a {@link ResearchEffect} type. One presenter serves both uses of an effect:
 * as an effect row of the effects group, and as the description of an action that applies it.
 * Mods register presenters for their own effect types via {@link ScholarshipEffectPresenterRegistry#register}.
 */
public interface ScholarshipEffectPresenter {

    /**
     * The row presenting the effect as an effect: its icons and a title starting with "Effect: ", plus optional details.
     * The group, the met state and the missing line are set by the caller. Effect rows are never clickable.
     */
    ScholarshipRow present(ResearchEffect effect, ScholarshipContext context);

    /**
     * The tooltip line describing what performing an action with this effect does, e.g. "Click to receive Research Note".
     * The action context tells e.g. whether the action is repeatable.
     */
    default Component line(ResearchEffect effect, ScholarshipActionContext context) {
        return Component.translatable(Gui.SCHOLARSHIP_ACTION_PERFORM);
    }

    /**
     * The reason the effect cannot be applied right now, or null if it looks available. Server stays the authority.
     */
    @Nullable
    default Component unavailableReason(ResearchEffect effect, Player player) {
        return null;
    }
}
