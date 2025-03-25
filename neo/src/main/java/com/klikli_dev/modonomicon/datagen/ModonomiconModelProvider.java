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
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

public class ModonomiconModelProvider extends ModelProvider {
    public ModonomiconModelProvider(PackOutput packOutput) {
        super(packOutput, Modonomicon.MOD_ID);
    }

    public final void registerItemFlat(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(Modonomicon.loc("item/" + texture)),
                itemModelGenerator.modelOutput);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        this.registerItemFlat(ItemRegistry.MODONOMICON.get(), "modonomicon_purple", itemModels);

        this.registerItemFlat(ItemRegistry.MODONOMICON_BLUE.get(), "modonomicon_blue", itemModels);
        this.registerItemFlat(ItemRegistry.MODONOMICON_GREEN.get(), "modonomicon_green", itemModels);
        this.registerItemFlat(ItemRegistry.MODONOMICON_PURPLE.get(), "modonomicon_purple", itemModels);
        this.registerItemFlat(ItemRegistry.MODONOMICON_RED.get(), "modonomicon_red", itemModels);
        this.registerItemFlat(ItemRegistry.LEAFLET.get(), "leaflet", itemModels);
    }
}
