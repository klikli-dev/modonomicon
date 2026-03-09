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
import net.minecraft.client.data.models.model.*;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ModonomiconModelProvider extends ModelProvider {
    public ModonomiconModelProvider(PackOutput packOutput) {
        super(packOutput, Modonomicon.MOD_ID);
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
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        this.generateFlatItem(ItemRegistry.MODONOMICON.get(), "_purple", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_BLUE.get(), "", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_GREEN.get(), "", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_PURPLE.get(), "", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_RED.get(), "red", itemModels);
        this.generateFlatItem(ItemRegistry.LEAFLET.get(), "", itemModels);
    }
}
