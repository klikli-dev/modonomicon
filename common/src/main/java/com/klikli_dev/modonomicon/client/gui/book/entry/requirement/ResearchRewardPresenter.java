/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.reward.ResearchReward;

/**
 * Client side presentation of a {@link ResearchReward} type, shown as a row of the rewards category.
 * Mods can register presenters for their own reward types, see {@link ResearchRewardPresenterRegistry}.
 */
public interface ResearchRewardPresenter {

    /**
     * The row presenting the reward: its icons and a title starting with "Reward: ", plus optional details.
     * The category, the met state and the missing line are set by the caller. Reward rows are never clickable.
     */
    RequirementRow present(ResearchReward reward, RequirementContext context);
}
