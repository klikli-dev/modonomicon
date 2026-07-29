// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

public class NeoBookProvider {
    /**
     * Creates a new BookProvider wired to the given caches.
     *
     * @param event           the gather data event
     * @param langCache       the language provider cache
     * @param researchCache   the research cache
     * @param subProviders    the sub providers to generate books from
     * @return the book provider to register on the DataGenerator
     */
    public static BookProvider of(GatherDataEvent event, LanguageProviderCache langCache, ResearchCache researchCache, BookSubProvider... subProviders) {
        return new BookProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
                event.getModContainer().getModId(), List.of(subProviders), langCache, researchCache);
    }
}
