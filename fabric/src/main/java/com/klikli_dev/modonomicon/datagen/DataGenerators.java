// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");
        pack.addProvider((FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) ->
                new BookProvider(output, registriesFuture, Modonomicon.MOD_ID, List.of(
                        //Add our demo book provider to the book provider
                        new DemoBook(Modonomicon.MOD_ID, enUsCache),
                        new DemoLeaflet(Modonomicon.MOD_ID, enUsCache)
                ))
        );
        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        pack.addProvider((FabricDataOutput output) -> new EnUsProvider(output, enUsCache));

        pack.addProvider((FabricDataOutput output) -> new DemoMultiblockProvider(output, Modonomicon.MOD_ID));
        pack.addProvider(ItemModelProvider::new);
    }
}
