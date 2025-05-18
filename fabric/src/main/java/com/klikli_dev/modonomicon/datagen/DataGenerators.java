// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.AbstractModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.AddToModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.FabricBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.datagen.book.AddToDemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoBook;
import com.klikli_dev.modonomicon.datagen.book.DemoLeaflet;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        //We use a language cache that the book provider can write into
        var enUsCache = new LanguageProviderCache("en_us");

        //If extending an existing book in another mod, we create a separate cache for that (as that will be saved in another mod id)
        var addToLangCache = new LanguageProviderCache("en_us");

        pack.addProvider(FabricBookProvider.of(
                //Add our demo book sub provider to the book provider
                new DemoBook(Modonomicon.MOD_ID, enUsCache),
                //Add our demo leaflet sub provider to the book provider
                new DemoLeaflet(Modonomicon.MOD_ID, enUsCache)
                //Add our addon book provider which adds to theurgy's book
                //Disabled, otherwise modders that run the modonomicon datagen generate a theurgy addon ..
                //new AddToDemoBook(addToLangCache)
        ));
        //Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
        pack.addProvider((FabricDataOutput output) -> new EnUsProvider(output, enUsCache));

        //For our addon book we can use the AddToModonomiconLanguageProvider class which just writes the cache to the target modid
        pack.addProvider((FabricDataOutput output) -> new AddToModonomiconLanguageProvider(output, "theurgy", "en_us", addToLangCache));

        //Sample of a legacy book provider registration
//        pack.addProvider(FabricBookProvider.of(
//                (output, registries) -> new MyLegacyBookProvider("bookId", output, "modId", enUsCache)
//        ));

        pack.addProvider((FabricDataOutput output) -> new DemoMultiblockProvider(output, Modonomicon.MOD_ID));
        pack.addProvider(ItemModelProvider::new);

        pack.addProvider(ItemTagsProvider::new);
    }
}
