/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.AddToModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.datagen.book.AddToDemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");

        //If extending an existing book in another mod, we create a separate cache for that (as that will be saved in another mod id)
        var addToLangCache = new LanguageProviderCache("en_us");

        generator.addProvider(event.includeServer(), NeoBookProvider.of(event,
                        //Add our demo book sub provider to the book provider
                        new DemoBook(Modonomicon.MOD_ID, enUsCache),
                        //Add our demo leaflet sub provider to the book provider
                        new DemoLeaflet(Modonomicon.MOD_ID, enUsCache)
                        //Add our addon book provider which adds to theurgy's book
                        //Disabled, otherwise modders that run the modonomicon datagen generate a theurgy addon ..
                        //new AddToDemoBook(addToLangCache)
                )
        );
        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        generator.addProvider(event.includeClient(), new EnUsProvider(generator.getPackOutput(), enUsCache));

        //For our addon book we can use the AddToModonomiconLanguageProvider class which just writes the cache to the target modid
        generator.addProvider(event.includeClient(), new AddToModonomiconLanguageProvider(generator.getPackOutput(), "theurgy", "en_us", addToLangCache));


        //Sample of a legacy book provider registration
//        generator.addProvider(event.includeServer(), NeoBookProvider.of(event,
//                new MyLegacyBookProvider("bookId", generator.getPackOutput(), "modid", enUsCache)
//        ));

        generator.addProvider(event.includeServer(), new DemoMultiblockProvider(generator.getPackOutput(), Modonomicon.MOD_ID));
        generator.addProvider(event.includeClient(), new ItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));

        var blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeClient(), blockTagsProvider);
        generator.addProvider(event.includeClient(), new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), event.getExistingFileHelper()));
    }
}