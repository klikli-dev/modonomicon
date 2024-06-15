/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");
        generator.addProvider(event.includeServer(), new BookProvider(generator.getPackOutput(), event.getLookupProvider(), Modonomicon.MOD_ID, List.of(
                //Add our demo book provider to the book provider
                new DemoBook(Modonomicon.MOD_ID, enUsCache),
                new DemoLeaflet(Modonomicon.MOD_ID, enUsCache)
        )));
        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        generator.addProvider(event.includeClient(), new EnUsProvider(generator.getPackOutput(), enUsCache));

        generator.addProvider(event.includeServer(), new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));
        generator.addProvider(event.includeClient(), new ItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
    }
}