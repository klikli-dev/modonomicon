/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;

public class ModonomiconModelProvider extends FabricModelProvider {
    public ModonomiconModelProvider(FabricDataOutput packOutput) {
        super(packOutput);
    }

    public final void registerItemFlat(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(Modonomicon.loc("item/" + texture)),
                itemModelGenerator.modelOutput);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        this.registerItemFlat(ItemRegistry.MODONOMICON.get(), "modonomicon_purple", itemModelGenerator);

        this.registerItemFlat(ItemRegistry.MODONOMICON_BLUE.get(), "modonomicon_blue", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_GREEN.get(), "modonomicon_green", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_PURPLE.get(), "modonomicon_purple", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_RED.get(), "modonomicon_red", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.LEAFLET.get(), "leaflet", itemModelGenerator);
    }
}
