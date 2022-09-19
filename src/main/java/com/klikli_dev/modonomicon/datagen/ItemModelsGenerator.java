/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelsGenerator extends ItemModelProvider {
    public ItemModelsGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Modonomicon.MODID, existingFileHelper);
    }

    protected String name(Item item) {
        return Registry.ITEM.getKey(item).getPath();
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
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON.get()), "modonomicon_purple");

        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_BLUE.get()), "modonomicon_blue");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_GREEN.get()), "modonomicon_green");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_PURPLE.get()), "modonomicon_purple");
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON_RED.get()), "modonomicon_red");
        //this.registerItemGenerated("modonomicon_red", "modonomicon_red");
    }
}
