/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        var lang =  new LangGenerator.English(generator);
        generator.addProvider(event.includeClient(), new ItemModelsGenerator(generator, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementsGenerator(generator));
        generator.addProvider(event.includeServer(), new DemoBookProvider(generator, Modonomicon.MODID, lang));
        generator.addProvider(event.includeClient(), lang);

    }
}