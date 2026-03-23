/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ModonomiconModelProvider extends FabricModelProvider {
    public ModonomiconModelProvider(FabricPackOutput packOutput) {
        super(packOutput);
    }

    public void generateFlatItem(Item item, String suffix, ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(this.createFlatItemModel(item, suffix, itemModelGenerator)));
    }

    public Identifier createFlatItemModel(Item item, String suffix, ItemModelGenerators itemModelGenerator) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(TextureMapping.getItemTexture(item, suffix)),
                itemModelGenerator.modelOutput);
    }


    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        this.generateFlatItem(ItemRegistry.MODONOMICON.get(), "_purple", itemModelGenerator);
        this.generateFlatItem(ItemRegistry.MODONOMICON_BLUE.get(), "", itemModelGenerator);
        this.generateFlatItem(ItemRegistry.MODONOMICON_GREEN.get(), "", itemModelGenerator);
        this.generateFlatItem(ItemRegistry.MODONOMICON_PURPLE.get(), "", itemModelGenerator);
        this.generateFlatItem(ItemRegistry.MODONOMICON_RED.get(), "", itemModelGenerator);
        this.generateFlatItem(ItemRegistry.LEAFLET.get(), "", itemModelGenerator);
    }
}
