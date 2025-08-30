// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricBookProvider {

    public static FabricDataGenerator.Pack.RegistryDependentFactory<BookProvider> of(BookSubProvider... subProviders) {
        return (FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) -> new BookProvider(output, registriesFuture, output.getModId(), List.of(subProviders));
    }

    public static <T extends LegacyBookProvider> FabricDataGenerator.Pack.RegistryDependentFactory<BookProvider> of(LegacyBookProviderFactory<T> factory) {
        return (FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) -> new BookProvider(output, registriesFuture, output.getModId(), List.of(factory.create(output, registriesFuture)));
    }

    @FunctionalInterface
    public interface LegacyBookProviderFactory<T extends LegacyBookProvider> {
        T create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture);
    }
}
