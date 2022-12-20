/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Modonomicon.MODID, existingFileHelper);
    }

    protected String name(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    private void registerItemGenerated(String name) {
        this.registerItemGenerated(name, name);
    }

    private void registerItemGenerated(String name, String texture) {
        this.getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", Modonomicon.loc("item/" + texture));
    }

    private void registerItemHandheld(String name) {
        this.getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", Modonomicon.loc("item/" + name));
    }

    @Override
    protected void registerModels() {
        //moved to manually created json, because we have a custom loader and only one instance
        //this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON.get()), "modonomicon_purple");

        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_BLUE.get()), "modonomicon_blue");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_GREEN.get()), "modonomicon_green");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_PURPLE.get()), "modonomicon_purple");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_RED.get()), "modonomicon_red");
    }
}
