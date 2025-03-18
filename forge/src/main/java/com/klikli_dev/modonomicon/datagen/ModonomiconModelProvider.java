/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModonomiconModelProvider extends ModelProvider {
    public ModonomiconModelProvider(PackOutput packOutput) {
        super(packOutput, Modonomicon.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        //default modonomicon item model is the purple model
        itemModels.createFlatItemModel(ItemRegistry.MODONOMICON.get(), "_purple", ModelTemplates.FLAT_ITEM);

        //register all other modonomicon helper items that force model loading
        itemModels.createFlatItemModel(ItemRegistry.MODONOMICON_BLUE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.createFlatItemModel(ItemRegistry.MODONOMICON_GREEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.createFlatItemModel(ItemRegistry.MODONOMICON_PURPLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.createFlatItemModel(ItemRegistry.MODONOMICON_RED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.createFlatItemModel(ItemRegistry.LEAFLET.get(), ModelTemplates.FLAT_ITEM);
    }
}
