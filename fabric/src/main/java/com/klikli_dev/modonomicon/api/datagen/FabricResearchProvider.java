/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

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
     * Creates the platform-specific factory that instantiates {@link ResearchProvider} with the
     * given research subproviders.
     */
    public static FabricDataGenerator.Pack.RegistryDependentFactory<ResearchProvider> of(ResearchSubProvider... subProviders) {
        return (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) ->
                new ResearchProvider(output, registriesFuture, output.getModId(), List.of(subProviders));
    }
}
