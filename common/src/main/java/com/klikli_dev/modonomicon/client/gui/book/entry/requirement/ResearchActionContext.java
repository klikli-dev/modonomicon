/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.network.chat.Component;

/**
 * What an effect is presented for when it is part of an action: the action itself and the title of the row the action is on.
 *
 * @param action the action the effect belongs to, e.g. to tell repeatable actions apart
 * @param factTitle the title of the requirement row the action is attached to
 * @param performed whether the row the action is attached to is already met, i.e. the action was performed at least once
 */
public record ResearchActionContext(ResearchAction action, Component factTitle, boolean performed) {

    public ResearchActionContext(ResearchAction action, Component factTitle) {
        this(action, factTitle, false);
    }
}
