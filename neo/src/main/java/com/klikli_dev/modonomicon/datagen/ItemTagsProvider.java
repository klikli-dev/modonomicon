// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        //noinspection deprecation
        super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key(), Modonomicon.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //item tag provider is per modloader because forge and neo modify the constructor
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(ItemRegistry.MODONOMICON.get());
        this.tag(ItemTags.LECTERN_BOOKS).add(ItemRegistry.MODONOMICON.get());
    }
}
