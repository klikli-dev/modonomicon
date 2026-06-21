/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.ForgeBookProvider;
import com.klikli_dev.modonomicon.api.datagen.ForgeResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoIndexBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        var langCache = new LanguageProviderCache("en_us");
        var researchCache = new ResearchCache();

        generator.addProvider(event.includeServer(), ForgeBookProvider.of(event, langCache, researchCache,
                new DemoBook(),
                new DemoIndexBook(),
                new DemoLeaflet()
        ));
        generator.addProvider(event.includeServer(), ForgeResearchProvider.of(event, langCache, researchCache, new DemoResearch(Modonomicon.MOD_ID)));
        generator.addProvider(event.includeClient(), new EnUsProvider(generator.getPackOutput(), langCache));
        generator.addProvider(event.includeServer(), new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));
        generator.addProvider(event.includeClient(), new ModonomiconModelProvider(generator.getPackOutput()));

        var blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeClient(), blockTagsProvider);
        generator.addProvider(event.includeClient(), new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
    }
}
