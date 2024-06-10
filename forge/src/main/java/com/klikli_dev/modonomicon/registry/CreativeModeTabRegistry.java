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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Modonomicon.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MODONOMICON = CREATIVE_MODE_TABS.register(Modonomicon.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.MODONOMICON_PURPLE.get().getDefaultInstance())
            .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
            .build());


    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        var tabName = net.minecraftforge.common.CreativeModeTabRegistry.getName(event.getTab());
        if (tabName == null)
            return;

        //From: Neo EventHooks#onCreativeModeTabBuildContents
        //we need to use it here to test before inserting, because event.getEntries().contains uses a different hashing strategy and is thus not reliable
        final var searchDupes = new ObjectLinkedOpenCustomHashSet<ItemStack>(ItemStackLinkedSet.TYPE_AND_TAG);

        BookDataManager.get().getBooks().values().forEach(b -> {
            if (event.getTabKey() == CreativeModeTabs.SEARCH || net.minecraftforge.common.CreativeModeTabRegistry.getTab(ResourceLocation.parse(b.getCreativeTab())) == event.getTab()) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());

                    CompoundTag cmp = new CompoundTag();
                    cmp.putString(ModonomiconConstants.Nbt.ITEM_BOOK_ID_TAG, b.getId().toString());

                    stack.set(DataComponentRegistry.BOOK_ID.get(), b.getId());

                    if (searchDupes.add(stack)) {
                        event.accept(stack, event.getTabKey() == CreativeModeTabs.SEARCH ? CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY : CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                }
            }
        });
    }

}
