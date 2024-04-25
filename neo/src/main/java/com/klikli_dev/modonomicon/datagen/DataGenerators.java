/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBookProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var registries = event.getLookupProvider();


        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");

        generator.addProvider(event.includeServer(), new DemoBookProvider(generator.getPackOutput(), registries, Modonomicon.MOD_ID, enUsCache));
        generator.addProvider(event.includeServer(), new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));

        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        generator.addProvider(event.includeClient(), new EnUsProvider(generator.getPackOutput(), enUsCache));

        generator.addProvider(event.includeClient(), new ItemModelProvider(generator.getPackOutput(), existingFileHelper));
    }
}