/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.LegacyBookProvider;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
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
        generator.addProvider(NeoBookProvider.of(event,
                //Add our demo book sub provider to the book provider
                new DemoBook(Modonomicon.MOD_ID, enUsCache),
                //Add our demo leaflet sub provider to the book provider
                new DemoLeaflet(Modonomicon.MOD_ID, enUsCache))
        );

        //Sample of a legacy book provider registration
//        generator.addProvider(event.includeServer(), NeoBookProvider.of(event,
//                new MyLegacyBookProvider("bookId", generator.getPackOutput(), "modid", enUsCache)
//        ));

        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        generator.addProvider(new EnUsProvider(generator.getPackOutput(), enUsCache));

        generator.addProvider(new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));
        generator.addProvider(new ItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));

        var blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), event.getExistingFileHelper()));
    }
}