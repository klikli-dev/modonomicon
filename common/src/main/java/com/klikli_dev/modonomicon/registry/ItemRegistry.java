/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ItemRegistry {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Modonomicon.MOD_ID);

    public static final RegistryObject<Item> MODONOMICON =
            ITEMS.register("modonomicon", () -> new ModonomiconItem(new Item.Properties()));

    //Dummy items for default models
    public static final RegistryObject<Item> MODONOMICON_BLUE =
            ITEMS.register("modonomicon_blue", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_GREEN =
            ITEMS.register("modonomicon_green", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_PURPLE =
            ITEMS.register("modonomicon_purple", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_RED =
            ITEMS.register("modonomicon_red", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LEAFLET =
            ITEMS.register("leaflet", () -> new Item(new Item.Properties()));

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }
}
