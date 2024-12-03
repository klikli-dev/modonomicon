// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ForgeBookProvider {

    /**
     * Creates a new BookProvider with the given subProviders. This is the main entry point for book generation, as each BookSubProvider represents one book or leaflet.
     * @param event the gather data event that will be used to access datagen context needed for the provider.
     * @param subProviders the sub providers to generate books from.
     * @return the book provider to register on the DataGenerator provided by the GatherDataEvent.
     */
    public static BookProvider of(GatherDataEvent event, BookSubProvider... subProviders) {
        return new BookProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getModContainer().getModId(), List.of(subProviders));
    }

    /**
     * Creates a new BookProvider with the given subProviders. This is the main entry point for book generation, as each BookSubProvider represents one book or leaflet.
     * This overload allows to pass a custom CompletableFuture for the HolderLookup.Provider. The main use case for this is to provide a HolderLookup.Provide returned by DatapackBuiltinEntriesProvider#getRegistryProvider() to access datapack registries in the book generation.
     * @param event the gather data event that will be used to access datagen context needed for the provider.
     * @param lookupProvider the lookup provider to use for the book generation.
     * @param subProviders the sub providers to generate books from.
     * @return the book provider to register on the DataGenerator provided by the GatherDataEvent.
     */
    public static BookProvider of(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookupProvider, BookSubProvider... subProviders) {
        return new BookProvider(event.getGenerator().getPackOutput(), lookupProvider, event.getModContainer().getModId(), List.of(subProviders));
    }
}
