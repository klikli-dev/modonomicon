/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class CreativeModeTabRegistry {

    public static final ResourceKey<CreativeModeTab> MODONOMICON_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Modonomicon.loc(Modonomicon.MOD_ID));
    public static final CreativeModeTab MODONOMICON = FabricItemGroup.builder()
            .icon(() -> ItemRegistry.MODONOMICON_PURPLE.get().getDefaultInstance())
            .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
            .build();

    public static void onModifyEntries(CreativeModeTab group, FabricItemGroupEntries entries) {
        var tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(group).get();
        BookDataManager.get().getBooks().values().forEach(b -> {
            if (tabKey == CreativeModeTabs.SEARCH ||
                    BuiltInRegistries.CREATIVE_MODE_TAB.get(new ResourceLocation(b.getCreativeTab())) == group) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());
                    
                    stack.set(DataComponentRegistry.BOOK_ID.get(), b.getId());

                    entries.accept(stack);
                }
            }
        });
    }

}
