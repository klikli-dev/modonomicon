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

    //Dummy items for default models
    public static final RegistryObject<Item> MODONOMICON_BLUE =
            ITEMS.register("modonomicon_blue", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_GREEN =
            ITEMS.register("modonomicon_greem", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_PURPLE =
            ITEMS.register("modonomicon_purple", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_RED =
            ITEMS.register("modonomicon_red", () -> new ModonomiconItem(new Item.Properties()));

    public static Item.Properties defaultProperties() {
        return new Item.Properties().tab(Modonomicon.CREATIVE_MODE_TAB);
    }
}
