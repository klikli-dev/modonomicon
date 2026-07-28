/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.minecraftforge.data.event.GatherDataEvent;

/**
 * Forge registration helper for the research datagen provider.
 */
public class ForgeResearchProvider {
    /**
     * Creates a {@link ResearchProvider} wired to the given research cache and language cache.
     *
     * @param event           the gather data event
     * @param langCache       the language cache
     * @param researchCache   the research cache
     * @param subProviders    the research sub providers
     * @return the research provider to register on the DataGenerator
     */
    public static ResearchProvider of(GatherDataEvent event, LanguageProviderCache langCache,
            ResearchCache researchCache, ResearchSubProvider... subProviders) {
        return new ResearchProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
                event.getModContainer().getModId(), java.util.List.of(subProviders), researchCache, langCache);
    }
}
