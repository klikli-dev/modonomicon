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
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModonomiconModelProvider extends ModelProvider {
    public ModonomiconModelProvider(PackOutput packOutput) {
        super(packOutput);
    }

    public void generateFlatItem(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(this.createFlatItemModel(item, texture, itemModelGenerator)));
    }

    public ResourceLocation createFlatItemModel(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(Modonomicon.loc("item/" + texture)),
                itemModelGenerator.modelOutput);
    }

    @Override
    protected Stream<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> Modonomicon.MOD_ID.equals(block.builtInRegistryHolder().key().location().getNamespace()));
    }

    @Override
    protected Stream<Item> getKnownItems() {
        return BuiltInRegistries.ITEM.stream().filter(item -> Modonomicon.MOD_ID.equals(item.builtInRegistryHolder().key().location().getNamespace()));
    }

    @Override
    protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
        var itemModels = super.getItemModelGenerators(items, models);

        //register our own
        this.registerItemModels(itemModels);
        return itemModels;
    }

    protected void registerItemModels(ItemModelGenerators itemModels) {
        //TODO: Currently forge causes all minecraft assets to be generated.

        this.generateFlatItem(ItemRegistry.MODONOMICON.get(), "modonomicon_purple", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_BLUE.get(), "modonomicon_blue", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_GREEN.get(), "modonomicon_green", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_PURPLE.get(), "modonomicon_purple", itemModels);
        this.generateFlatItem(ItemRegistry.MODONOMICON_RED.get(), "modonomicon_red", itemModels);
        this.generateFlatItem(ItemRegistry.LEAFLET.get(), "leaflet", itemModels);
    }
}
