/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

/**
 * Forge registration helper for the research datagen provider.
 */
public class ForgeResearchProvider {
    /**
     * Creates a {@link ResearchProvider} for the given Forge gather-data event and research
     * subproviders.
     */
    public static ResearchProvider of(GatherDataEvent event, ResearchSubProvider... subProviders) {
        return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getModContainer().getModId(), List.of(subProviders));
    }
}
