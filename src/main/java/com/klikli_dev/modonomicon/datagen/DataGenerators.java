/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        var lang = new LangGenerator.English(generator);

        if(event.includeServer()){
            generator.addProvider(new AdvancementsGenerator(generator));
            generator.addProvider(new DemoBookProvider(generator, Modonomicon.MODID, lang));
            generator.addProvider(new DemoMultiblockProvider(generator, Modonomicon.MODID));
        }

        if(event.includeClient()){
            //Important: Lang provider needs to be added after the book provider to process the texts added by the book provider
            generator.addProvider(lang);
            generator.addProvider(new ItemModelsGenerator(generator, existingFileHelper));
        }

    }
}