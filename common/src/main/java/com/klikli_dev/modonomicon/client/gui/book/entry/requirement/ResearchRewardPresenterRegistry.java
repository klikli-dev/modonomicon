/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.reward.ResearchReward;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research reward types, keyed by reward type id. Unregistered types show a generic "Special reward" row.
 */
public final class ResearchRewardPresenterRegistry {

    private static final Map<Identifier, ResearchRewardPresenter> PRESENTERS = new ConcurrentHashMap<>();

    static {
        DefaultResearchRewardPresenters.register();
    }

    private ResearchRewardPresenterRegistry() {
    }

    /**
     * Registers the presenter for a reward type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier rewardTypeId, ResearchRewardPresenter presenter) {
        PRESENTERS.put(rewardTypeId, presenter);
    }

    /**
     * Presents the reward as a row of the rewards category. The caller sets category, met state and missing line.
     */
    public static RequirementRow present(ResearchReward reward, RequirementContext context) {
        var presenter = PRESENTERS.get(reward.type().id());
        if (presenter != null) {
            return presenter.present(reward, context);
        }
        return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.BOOK))), Component.translatable(Gui.REQUIREMENTS_REWARD_SPECIAL));
    }
}
