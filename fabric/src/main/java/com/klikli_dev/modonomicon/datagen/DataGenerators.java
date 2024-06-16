// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBookProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");

        pack.addProvider((FabricDataOutput output) -> new DemoBookProvider(output, Modonomicon.MOD_ID, enUsCache));
        pack.addProvider((FabricDataOutput output) -> new DemoMultiblockProvider(output, Modonomicon.MOD_ID));

        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        pack.addProvider((FabricDataOutput output) -> new EnUsProvider(output, enUsCache));

        pack.addProvider(ItemModelProvider::new);
        pack.addProvider(ItemTagsProvider::new);
    }
}
