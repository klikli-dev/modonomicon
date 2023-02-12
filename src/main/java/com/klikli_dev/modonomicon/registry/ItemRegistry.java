/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    //TODO: make mod loader agnostic
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Modonomicon.MODID);

    public static final RegistryObject<Item> MODONOMICON =
            ITEMS.register("modonomicon", () -> new ModonomiconItem(new Item.Properties()));

    //Dummy items for default models
    public static final RegistryObject<Item> MODONOMICON_BLUE =
            ITEMS.register("modonomicon_blue", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_GREEN =
            ITEMS.register("modonomicon_green", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_PURPLE =
            ITEMS.register("modonomicon_purple", () -> new ModonomiconItem(new Item.Properties()));
    public static final RegistryObject<Item> MODONOMICON_RED =
            ITEMS.register("modonomicon_red", () -> new ModonomiconItem(new Item.Properties()));

    public static void onRegisterCreativeModeTabs(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(Modonomicon.loc(Modonomicon.MODID),
                (b) -> b
                        .icon(() -> new ItemStack(MODONOMICON_PURPLE.get()))
                        .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
                        .build()
        );
    }
}
