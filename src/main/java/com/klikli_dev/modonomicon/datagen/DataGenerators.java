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

        if(event.includeClient()){
            generator.addProvider(new LangGenerator.English(generator));
            generator.addProvider(new ItemModelsGenerator(generator, existingFileHelper));
        }

        if(event.includeServer()){
            generator.addProvider(new AdvancementsGenerator(generator));
            generator.addProvider(new BookGenerator(generator, Modonomicon.MODID));
        }

    }
}