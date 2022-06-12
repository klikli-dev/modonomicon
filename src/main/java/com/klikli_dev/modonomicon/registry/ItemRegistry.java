/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    //TODO: make mod loader agnostic
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Modonomicon.MODID);

    public static final RegistryObject<Item> MODONOMICON =
            ITEMS.register("modonomicon", () -> new ModonomiconItem(defaultProperties()));

    public static Item.Properties defaultProperties() {
        return new Item.Properties().tab(Modonomicon.CREATIVE_MODE_TAB);
    }
}
