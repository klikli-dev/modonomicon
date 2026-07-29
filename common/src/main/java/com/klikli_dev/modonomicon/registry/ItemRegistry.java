/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ItemRegistry {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Modonomicon.MOD_ID);

    public static final RegistryObject<Item> MODONOMICON =
            register("modonomicon", (properties) -> Services.PLATFORM.createModonomiconItem(properties.stacksTo(1)));

    //Dummy items for default models
    public static final RegistryObject<Item> MODONOMICON_BLUE =
            register("modonomicon_blue", Item::new);
    public static final RegistryObject<Item> MODONOMICON_GREEN =
            register("modonomicon_green", Item::new);
    public static final RegistryObject<Item> MODONOMICON_PURPLE =
            register("modonomicon_purple", Item::new);
    public static final RegistryObject<Item> MODONOMICON_RED =
            register("modonomicon_red", Item::new);
    public static final RegistryObject<Item> LEAFLET =
            register("leaflet", (properties) -> new Item(properties.stacksTo(1)));

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }


    public static <I extends Item> RegistryObject<I> register(final String name, final Function<Item.Properties, ? extends I> itemConstructor) {
        return ITEMS.register(name, () -> itemConstructor.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, name)))));
    }

}
