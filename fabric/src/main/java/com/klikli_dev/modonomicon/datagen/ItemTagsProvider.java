// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {

    public ItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider pProvider) {
        //item tag provider is per modloader because forge and neo modify the constructor
        this.builder(ItemTags.BOOKSHELF_BOOKS).add(ItemRegistry.MODONOMICON.getResourceKey());
        this.builder(ItemTags.LECTERN_BOOKS).add(ItemRegistry.MODONOMICON.getResourceKey());
    }
}
