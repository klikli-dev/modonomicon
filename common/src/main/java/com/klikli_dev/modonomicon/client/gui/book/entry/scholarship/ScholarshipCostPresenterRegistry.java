/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.data.ResearchGroups;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research cost types, keyed by cost type id. Unregistered types fall back to a generic row.
 */
public final class ScholarshipCostPresenterRegistry {

    private static final Map<Identifier, ScholarshipCostPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ScholarshipCostPresenter FALLBACK = (entry, context, forceMet) ->
            ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.GENERIC)), Component.translatable(Gui.SCHOLARSHIP_COST_SPECIAL))
                    .withGroup(ScholarshipRows.group(ResearchGroups.ITEMS, context));

    private ScholarshipCostPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in cost types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultScholarshipCostPresenters.register();
    }

    /**
     * Registers the presenter for a cost type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier costTypeId, ScholarshipCostPresenter presenter) {
        PRESENTERS.put(costTypeId, presenter);
    }

    /**
     * The presenter of a cost type, or a generic presenter if none is registered.
     */
    public static ScholarshipCostPresenter get(Identifier costTypeId) {
        return PRESENTERS.getOrDefault(costTypeId, FALLBACK);
    }
}
