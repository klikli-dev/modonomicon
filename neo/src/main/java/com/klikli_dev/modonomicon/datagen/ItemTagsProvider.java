// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags) {
        super(output, lookupProvider, pBlockTags, Modonomicon.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //item tag provider is per modloader because forge and neo modify the constructor
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(ItemRegistry.MODONOMICON.get());
        this.tag(ItemTags.LECTERN_BOOKS).add(ItemRegistry.MODONOMICON.get());
    }
}
