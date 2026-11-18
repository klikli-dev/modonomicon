/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.klikli_dev.modonomicon.research.reward.AddValueReward;
import com.klikli_dev.modonomicon.research.reward.ExperienceReward;
import com.klikli_dev.modonomicon.research.reward.GrantFactReward;
import com.klikli_dev.modonomicon.research.reward.ItemReward;
import com.klikli_dev.modonomicon.research.reward.ResearchReward;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

final class DefaultResearchRewardPresenters {

    private DefaultResearchRewardPresenters() {
    }

    static void register() {
        ResearchRewardPresenterRegistry.register(ResearchRewardTypeRegistry.ITEM.id(), (reward, context) -> {
            var stack = ((ItemReward) reward).stack().create();
            return RequirementRow.of(List.of(RequirementIcon.of(stack)), Component.translatable(Gui.REQUIREMENTS_REWARD, stack.getHoverName()));
        });

        ResearchRewardPresenterRegistry.register(ResearchRewardTypeRegistry.EXPERIENCE.id(), (reward, context) -> {
            var experience = (ExperienceReward) reward;
            var amount = experience.levels()
                    ? Component.translatable(Gui.REQUIREMENTS_REWARD_LEVELS, experience.amount())
                    : Component.translatable(Gui.REQUIREMENTS_REWARD_EXPERIENCE, experience.amount());
            return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.EXPERIENCE_BOTTLE))), Component.translatable(Gui.REQUIREMENTS_REWARD, amount));
        });

        ResearchRewardPresenterRegistry.register(ResearchRewardTypeRegistry.GRANT_FACT.id(), (reward, context) -> {
            var factId = ((GrantFactReward) reward).factId();
            var fact = ResearchRequirementRows.fact(factId, context, false);
            return fact.withTitle(Component.translatable(Gui.REQUIREMENTS_REWARD, fact.title()));
        });

        ResearchRewardPresenterRegistry.register(ResearchRewardTypeRegistry.ADD_VALUE.id(), (reward, context) -> {
            var add = (AddValueReward) reward;
            var definition = context.data().valueDefinitions().get(add.valueId());
            var display = definition != null ? definition.display().orElse(null) : null;
            var icon = display != null && display.icon().isPresent()
                    ? RequirementIcon.of(display.icon().get())
                    : RequirementIcon.of(new ItemStack(Items.NETHER_STAR));
            var name = display != null && display.title().isPresent()
                    ? display.title().get()
                    : Component.literal(add.valueId().getPath());
            return RequirementRow.of(List.of(icon), Component.translatable(Gui.REQUIREMENTS_REWARD,
                    Component.translatable(Gui.REQUIREMENTS_REWARD_VALUE, add.amount(), name)));
        });

        ResearchRewardPresenterRegistry.register(ResearchRewardTypeRegistry.FUNCTION.id(), (reward, context) ->
                RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.COMMAND_BLOCK))), Component.translatable(Gui.REQUIREMENTS_REWARD_SPECIAL)));
    }
}
