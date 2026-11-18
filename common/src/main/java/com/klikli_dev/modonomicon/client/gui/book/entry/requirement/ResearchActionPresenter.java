/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Client side presentation of a {@link ResearchAction} type.
 * The server remains the authority, presenters only describe the action and pre-check it for feedback.
 */
public interface ResearchActionPresenter {

    /**
     * The tooltip line describing what clicking does, e.g. "Click to receive Research Note".
     *
     * @param factTitle the title of the requirement row the action is attached to
     */
    Component line(ResearchAction action, Component factTitle);

    /**
     * The reason the action cannot be performed right now, or null if it looks available. Costs are not part of this.
     */
    @Nullable
    Component unavailableReason(ResearchAction action, Player player);
}
