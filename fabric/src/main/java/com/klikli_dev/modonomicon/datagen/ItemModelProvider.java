/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;

public class ItemModelProvider extends FabricModelProvider {
    public ItemModelProvider(FabricDataOutput packOutput) {
        super(packOutput);
    }

    public final void registerItemFlat(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(Modonomicon.loc("item/" + texture)),
                itemModelGenerator.output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        this.registerItemFlat(ItemRegistry.MODONOMICON_BLUE.get(), "modonomicon_blue", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_GREEN.get(), "modonomicon_green", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_PURPLE.get(), "modonomicon_purple", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.MODONOMICON_RED.get(), "modonomicon_red", itemModelGenerator);
        this.registerItemFlat(ItemRegistry.LEAFLET.get(), "leaflet", itemModelGenerator);
    }
}
