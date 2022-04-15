/*
 * LGPL-3.0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
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
        return item.getRegistryName().getPath();
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
        this.registerItemGenerated(this.name(ItemRegistry.MODONOMICON.get()), "modonomicon");
    }
}
