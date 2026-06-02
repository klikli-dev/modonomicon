/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.api.datagen.NeoResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoIndexBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        var langCache = new LanguageProviderCache("en_us");
        var researchCache = new ResearchCache();

        generator.addProvider(true, NeoBookProvider.of(event, langCache, researchCache,
                new DemoBook(),
                new DemoIndexBook(),
                new DemoLeaflet()
        ));
        generator.addProvider(true, NeoResearchProvider.of(event, langCache, researchCache, new DemoResearch(Modonomicon.MOD_ID)));
        generator.addProvider(true, new EnUsProvider(generator.getPackOutput(), langCache));
        generator.addProvider(true, new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));
        generator.addProvider(true, new ModonomiconModelProvider(generator.getPackOutput()));

        var blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider());
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider()));
    }
}
