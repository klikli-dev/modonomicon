// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.FabricBookProvider;
import com.klikli_dev.modonomicon.api.datagen.FabricResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoIndexBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        var langCache = new LanguageProviderCache("en_us");
        var researchCache = new ResearchCache();

        pack.addProvider(FabricBookProvider.of(Modonomicon.MOD_ID, langCache, researchCache,
                new DemoBook(),
                new DemoIndexBook(),
                new DemoLeaflet()
        ));
        pack.addProvider(FabricResearchProvider.of(Modonomicon.MOD_ID, langCache, researchCache, new DemoResearch(Modonomicon.MOD_ID)));
        pack.addProvider((FabricPackOutput output) -> new EnUsProvider(output, langCache));
        pack.addProvider((FabricPackOutput output) -> new DemoMultiblockProvider(output, Modonomicon.MOD_ID));
        pack.addProvider(ModonomiconModelProvider::new);
        pack.addProvider(ItemTagsProvider::new);
    }
}
