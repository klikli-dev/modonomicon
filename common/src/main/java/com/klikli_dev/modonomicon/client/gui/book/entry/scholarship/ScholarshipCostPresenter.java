/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import net.minecraft.network.chat.Component;

/**
 * Client side presentation of a {@link ResearchCostEntry} type. Both the requirement rows of a manual stage and the tooltip
 * lines of an action describe a cost through the same presenter, so each entry is described in one place.
 * Mods register presenters for their own cost types via {@link ScholarshipCostPresenterRegistry#register}.
 */
public interface ScholarshipCostPresenter {

    /**
     * The requirement row of the entry, with its icons, title, progress and met state. Completed costs pass forceMet.
     * The caller adds the "consumed on completion" detail.
     */
    ScholarshipRow row(ResearchCostEntry entry, ScholarshipContext context, boolean forceMet);

    /**
     * The tooltip line of the entry. Defaults to the entry's own description.
     */
    default Component line(ResearchCostEntry entry) {
        return entry.describe();
    }
}
