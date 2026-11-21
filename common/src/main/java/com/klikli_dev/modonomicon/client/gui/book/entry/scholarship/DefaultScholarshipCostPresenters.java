/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.cost.DurabilityCostEntry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.LevelsCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchGroups;
import net.minecraft.network.chat.Component;

import java.util.List;

final class DefaultScholarshipCostPresenters {

    private DefaultScholarshipCostPresenters() {
    }

    static void register() {
        ScholarshipCostPresenterRegistry.register(ResearchCostTypeRegistry.ITEM.id(), (entry, context, forceMet) -> {
            var item = (ItemCostEntry) entry;
            int target = item.count();
            int held = forceMet ? target : Math.min(item.heldCount(context.player()), target);
            return ScholarshipRow.of(List.of(ScholarshipIcon.of(item.item().displayStack())), item.item().displayName())
                    .withGroup(ScholarshipRows.group(ResearchGroups.ITEMS, context))
                    .withProgress(new ScholarshipRow.Progress(held, target))
                    .withMet(held >= target);
        });

        ScholarshipCostPresenterRegistry.register(ResearchCostTypeRegistry.DURABILITY.id(), (entry, context, forceMet) -> {
            var durability = (DurabilityCostEntry) entry;
            boolean met = forceMet || durability.heldCount(context.player()) > 0;
            return ScholarshipRow.of(List.of(ScholarshipIcon.of(durability.item().displayStack())),
                            Component.translatable(Gui.SCHOLARSHIP_COST_DURABILITY, durability.amount(), durability.item().displayName()))
                    .withGroup(ScholarshipRows.group(ResearchGroups.ITEMS, context))
                    .withMet(met)
                    .withMissing(Component.translatable(Gui.SCHOLARSHIP_COST_NOT_HELD));
        });

        ScholarshipCostPresenterRegistry.register(ResearchCostTypeRegistry.LEVELS.id(), (entry, context, forceMet) -> {
            var levels = (LevelsCostEntry) entry;
            int target = levels.amount();
            int held = forceMet ? target : Math.min(levels.heldCount(context.player()), target);
            return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.COST_LEVELS)),
                            Component.translatable(Gui.SCHOLARSHIP_COST_LEVELS, target))
                    .withGroup(ScholarshipRows.group(ResearchGroups.PROGRESS, context))
                    .withProgress(new ScholarshipRow.Progress(held, target))
                    .withMet(held >= target);
        });
    }
}
