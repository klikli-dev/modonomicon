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
        var tabName = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);
        var tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(group).get();
        BookDataManager.get().getBooks().values().forEach(b -> {

            if (tabKey == CreativeModeTabs.SEARCH || b.getCreativeTab().equals(tabName.toString())) {
                if (b.generateBookItem()) {
                    ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());

                    CompoundTag cmp = new CompoundTag();
                    cmp.putString(ModonomiconConstants.Nbt.ITEM_BOOK_ID_TAG, b.getId().toString());
                    stack.setTag(cmp);

                    if (entries.getDisplayStacks().stream().noneMatch(s -> s.hasTag()
                            && s.getTag().contains(ModonomiconConstants.Nbt.ITEM_BOOK_ID_TAG)
                            && s.getTag().getString(ModonomiconConstants.Nbt.ITEM_BOOK_ID_TAG).equals(b.getId().toString()))) {
                        entries.accept(stack);
                    }
                }
            }
        });
    }

}
