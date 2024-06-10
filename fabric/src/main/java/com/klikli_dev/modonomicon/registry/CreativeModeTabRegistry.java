/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.data.BookDataManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
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
import net.minecraft.world.item.ItemStackLinkedSet;

public class CreativeModeTabRegistry {

    public static final ResourceKey<CreativeModeTab> MODONOMICON_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Modonomicon.loc(Modonomicon.MOD_ID));
    public static final CreativeModeTab MODONOMICON = FabricItemGroup.builder()
            .icon(() -> ItemRegistry.MODONOMICON_PURPLE.get().getDefaultInstance())
            .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
            .build();

    public static void onModifyEntries(CreativeModeTab group, FabricItemGroupEntries entries) {
        var tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(group).get();

        //From: Neo EventHooks#onCreativeModeTabBuildContents
        //we need to use it here to test before inserting, because event.getEntries().contains uses a different hashing strategy and is thus not reliable
        final var searchDupes = ItemStackLinkedSet.createTypeAndComponentsSet();

        BookDataManager.get().getBooks().values().forEach(b -> {
            if (tabKey == CreativeModeTabs.SEARCH ||
                    BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.parse(b.getCreativeTab())) == group) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());
                    
                    stack.set(DataComponentRegistry.BOOK_ID.get(), b.getId());

                    if(searchDupes.add(stack))
                        entries.accept(stack, tabKey == CreativeModeTabs.SEARCH ? CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY : CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        });
    }

}
