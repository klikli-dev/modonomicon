/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import com.klikli_dev.modonomicon.research.effect.AddValueEffect;
import com.klikli_dev.modonomicon.research.effect.ExperienceEffect;
import com.klikli_dev.modonomicon.research.effect.GiveItemEffect;
import com.klikli_dev.modonomicon.research.effect.GrantFactEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class DefaultResearchEffectPresenters {

    private DefaultResearchEffectPresenters() {
    }

    static void register() {
        ResearchEffectPresenterRegistry.register(ResearchEffectTypeRegistry.GIVE_ITEM.id(), new ResearchEffectPresenter() {
            @Override
            public RequirementRow present(ResearchEffect effect, RequirementContext context) {
                var stack = ((GiveItemEffect) effect).stack().create();
                return RequirementRow.of(List.of(RequirementIcon.of(stack)), Component.translatable(Gui.REQUIREMENTS_REWARD, stack.getHoverName()));
            }

            @Override
            public Component line(ResearchEffect effect) {
                var stack = ((GiveItemEffect) effect).stack().create();
                return Component.translatable(Gui.REQUIREMENTS_ACTION_RECEIVE, stack.getHoverName());
            }

            @Override
            public @Nullable Component unavailableReason(ResearchEffect effect, Player player) {
                var give = (GiveItemEffect) effect;
                ItemStack stack = give.stack().create();
                if (give.requireAbsent() && !stack.isEmpty() && player.getInventory().contains(stack)) {
                    return Component.translatable(Gui.REQUIREMENTS_ACTION_ALREADY_HELD);
                }
                return null;
            }
        });

        ResearchEffectPresenterRegistry.register(ResearchEffectTypeRegistry.EXPERIENCE.id(), new ResearchEffectPresenter() {
            @Override
            public RequirementRow present(ResearchEffect effect, RequirementContext context) {
                var experience = (ExperienceEffect) effect;
                var amount = experience.levels()
                        ? Component.translatable(Gui.REQUIREMENTS_REWARD_LEVELS, experience.amount())
                        : Component.translatable(Gui.REQUIREMENTS_REWARD_EXPERIENCE, experience.amount());
                return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.EXPERIENCE_BOTTLE))), Component.translatable(Gui.REQUIREMENTS_REWARD, amount));
            }
        });

        ResearchEffectPresenterRegistry.register(ResearchEffectTypeRegistry.GRANT_FACT.id(), new ResearchEffectPresenter() {
            @Override
            public RequirementRow present(ResearchEffect effect, RequirementContext context) {
                var factId = ((GrantFactEffect) effect).factId();
                var fact = ResearchRequirementRows.fact(factId, context, false);
                return fact.withTitle(Component.translatable(Gui.REQUIREMENTS_REWARD, fact.title()));
            }
        });

        ResearchEffectPresenterRegistry.register(ResearchEffectTypeRegistry.ADD_VALUE.id(), new ResearchEffectPresenter() {
            @Override
            public RequirementRow present(ResearchEffect effect, RequirementContext context) {
                var add = (AddValueEffect) effect;
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
            }
        });

        ResearchEffectPresenterRegistry.register(ResearchEffectTypeRegistry.FUNCTION.id(), new ResearchEffectPresenter() {
            @Override
            public RequirementRow present(ResearchEffect effect, RequirementContext context) {
                return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.COMMAND_BLOCK))), Component.translatable(Gui.REQUIREMENTS_REWARD_SPECIAL));
            }
        });
    }
}
