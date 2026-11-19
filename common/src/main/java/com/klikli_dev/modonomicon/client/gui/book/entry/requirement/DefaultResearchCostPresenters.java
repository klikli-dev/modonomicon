/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.cost.DurabilityCostEntry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.LevelsCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
import net.minecraft.network.chat.Component;

import java.util.List;

final class DefaultResearchCostPresenters {

    private DefaultResearchCostPresenters() {
    }

    static void register() {
        ResearchCostPresenterRegistry.register(ResearchCostTypeRegistry.ITEM.id(), (entry, context, forceMet) -> {
            var item = (ItemCostEntry) entry;
            int target = item.count();
            int held = forceMet ? target : Math.min(item.heldCount(context.player()), target);
            return RequirementRow.of(List.of(RequirementIcon.of(item.item().displayStack()).withBadge(ResearchIcons.BADGE_CONSUMED)), item.item().displayName())
                    .withCategory(ResearchRequirementRows.category(ResearchCategories.ITEMS, context))
                    .withProgress(new RequirementRow.Progress(held, target))
                    .withMet(held >= target);
        });

        ResearchCostPresenterRegistry.register(ResearchCostTypeRegistry.DURABILITY.id(), (entry, context, forceMet) -> {
            var durability = (DurabilityCostEntry) entry;
            boolean met = forceMet || durability.heldCount(context.player()) > 0;
            return RequirementRow.of(List.of(RequirementIcon.of(durability.item().displayStack()).withBadge(ResearchIcons.BADGE_DURABILITY)),
                            Component.translatable(Gui.REQUIREMENTS_COST_DURABILITY, durability.amount(), durability.item().displayName()))
                    .withCategory(ResearchRequirementRows.category(ResearchCategories.ITEMS, context))
                    .withMet(met)
                    .withMissing(Component.translatable(Gui.REQUIREMENTS_COST_NOT_HELD));
        });

        ResearchCostPresenterRegistry.register(ResearchCostTypeRegistry.LEVELS.id(), (entry, context, forceMet) -> {
            var levels = (LevelsCostEntry) entry;
            int target = levels.amount();
            int held = forceMet ? target : Math.min(levels.heldCount(context.player()), target);
            return RequirementRow.of(List.of(ResearchIcons.icon(ResearchIcons.COST_LEVELS)),
                            Component.translatable(Gui.REQUIREMENTS_COST_LEVELS, target))
                    .withCategory(ResearchRequirementRows.category(ResearchCategories.PROGRESS, context))
                    .withProgress(new RequirementRow.Progress(held, target))
                    .withMet(held >= target);
        });
    }
}
