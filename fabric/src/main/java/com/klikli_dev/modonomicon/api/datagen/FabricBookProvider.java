// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricBookProvider {

    /**
     * Creates a factory for a BookProvider wired to the given caches.
     *
     * @param modId           the mod id
     * @param langCache       the language provider cache
     * @param researchCache   the research cache
     * @param subProviders    the sub providers to generate books from
     * @return a factory to register with {@link FabricDataGenerator.Pack#addProvider}
     */
    public static FabricDataGenerator.Pack.RegistryDependentFactory<BookProvider> of(
            String modId, LanguageProviderCache langCache, ResearchCache researchCache, BookSubProvider... subProviders) {
        return (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) ->
                new BookProvider(output, registries, modId, List.of(subProviders), langCache, researchCache);
    }
}
