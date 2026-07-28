/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric registration helper for the research datagen provider.
 */
public class FabricResearchProvider {
    /**
     * Creates a factory for a ResearchProvider wired to the given research cache and language cache.
     *
     * @param modId           the mod id
     * @param langCache       the language cache
     * @param researchCache   the research cache
     * @param subProviders    the research sub providers
     * @return a factory to register with {@link FabricDataGenerator.Pack#addProvider}
     */
    public static FabricDataGenerator.Pack.RegistryDependentFactory<ResearchProvider> of(
            String modId, LanguageProviderCache langCache,
            ResearchCache researchCache, ResearchSubProvider... subProviders) {
        return (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) ->
                new ResearchProvider(output, registries, modId, List.of(subProviders), researchCache, langCache);
    }
}
