/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research cost types, keyed by cost type id. Unregistered types fall back to a generic row.
 */
public final class ResearchCostPresenterRegistry {

    private static final Map<Identifier, ResearchCostPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ResearchCostPresenter FALLBACK = (entry, context, forceMet) ->
            RequirementRow.of(List.of(ResearchIcons.icon(ResearchIcons.GENERIC)), Component.translatable(Gui.REQUIREMENTS_COST_SPECIAL))
                    .withCategory(ResearchRequirementRows.category(ResearchCategories.ITEMS, context));

    private ResearchCostPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in cost types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultResearchCostPresenters.register();
    }

    /**
     * Registers the presenter for a cost type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier costTypeId, ResearchCostPresenter presenter) {
        PRESENTERS.put(costTypeId, presenter);
    }

    /**
     * The presenter of a cost type, or a generic presenter if none is registered.
     */
    public static ResearchCostPresenter get(Identifier costTypeId) {
        return PRESENTERS.getOrDefault(costTypeId, FALLBACK);
    }
}
