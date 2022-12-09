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

        var lang = new LangGenerator.English(generator);

        generator.addProvider(event.includeServer(), new DemoBookProvider(generator, Modonomicon.MODID, lang));

        //Important: Lang provider needs to be added after the book provider to process the texts added by the book provider
        generator.addProvider(event.includeClient(), lang);
        generator.addProvider(event.includeClient(), new ItemModelProvider(generator, existingFileHelper));
    }
}