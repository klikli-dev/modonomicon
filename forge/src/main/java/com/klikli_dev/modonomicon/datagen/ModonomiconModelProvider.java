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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ModonomiconModelProvider extends ModelProvider {
    public ModonomiconModelProvider(PackOutput packOutput) {
        super(packOutput);
    }

    public static void generateFlatItem(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(createFlatItemModel(item, texture, itemModelGenerator)));
    }

    public static Identifier createFlatItemModel(Item item, String texture, ItemModelGenerators itemModelGenerator) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(Modonomicon.loc("item/" + texture)),
                itemModelGenerator.modelOutput);
    }

    @Override
    protected @NotNull Stream<Block> getKnownBlocks() {
        //noinspection deprecation
        return BuiltInRegistries.BLOCK.stream().filter(block -> Modonomicon.MOD_ID.equals(block.builtInRegistryHolder().key().location().getNamespace()));
    }

    @Override
    protected @NotNull Stream<Item> getKnownItems() {
        //noinspection deprecation
        return BuiltInRegistries.ITEM.stream().filter(item -> Modonomicon.MOD_ID.equals(item.builtInRegistryHolder().key().location().getNamespace()));
    }

    @Override
    protected @NotNull ItemModelGenerators getItemModelGenerators(@NotNull ItemInfoCollector items, @NotNull SimpleModelCollector models) {
        return new ItemModelGenerators(items, models) {
            @Override
            public void run() {
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.MODONOMICON.get(), "modonomicon_purple", this);
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.MODONOMICON_BLUE.get(), "modonomicon_blue", this);
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.MODONOMICON_GREEN.get(), "modonomicon_green", this);
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.MODONOMICON_PURPLE.get(), "modonomicon_purple", this);
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.MODONOMICON_RED.get(), "modonomicon_red", this);
                ModonomiconModelProvider.generateFlatItem(ItemRegistry.LEAFLET.get(), "leaflet", this);
            }
        };
    }

    @Override
    protected @NotNull BlockModelGenerators getBlockModelGenerators(@NotNull BlockStateGeneratorCollector blocks, @NotNull ItemInfoCollector items, @NotNull SimpleModelCollector models) {
        return new BlockModelGenerators(blocks, items, models) {
            @Override
            public void run() {
            }
        };
    }
}
