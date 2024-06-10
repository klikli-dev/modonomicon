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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Modonomicon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MODONOMICON = CREATIVE_MODE_TABS.register(Modonomicon.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.MODONOMICON_PURPLE.get().getDefaultInstance())
            .title(Component.translatable(ModonomiconConstants.I18n.ITEM_GROUP))
            .build());


    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        var tabName = net.neoforged.neoforge.common.CreativeModeTabRegistry.getName(event.getTab());
        if (tabName == null)
            return;

        //From: EventHooks#onCreativeModeTabBuildContents
        //we need to use it here to test before inserting, because event.getEntries().contains uses a different hashing strategy and is thus not reliable
        final var searchDupes = new ObjectLinkedOpenCustomHashSet<ItemStack>(ItemStackLinkedSet.TYPE_AND_TAG);

        BookDataManager.get().getBooks().values().forEach(b -> {
            if (event.getTabKey() == CreativeModeTabs.SEARCH || BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.parse(b.getCreativeTab())) == event.getTab()) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());

                    stack.set(DataComponentRegistry.BOOK_ID.get(), b.getId());

                    if (searchDupes.add(stack)) {
                        event.accept(stack, event.getTabKey() == CreativeModeTabs.SEARCH ? CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY : CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                }
            }
        });
    }

}
