/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BookModel implements ItemModel {
    private final ItemModel original;

    public BookModel(ItemModel original) {
        this.original = original;
    }

    public static void replace(Map<ResourceLocation, ItemModel> models) {
        models.computeIfPresent(ItemRegistry.MODONOMICON.getId(), (k, oldModel) -> new BookModel(oldModel));
    }


    @Override
    public void update(@NotNull ItemStackRenderState renderState, @NotNull ItemStack stack, @NotNull ItemModelResolver itemModelResolver, @NotNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        var book = ModonomiconItem.getBook(stack);
        if (book != null) {
            var itemModel = Minecraft.getInstance().getModelManager().getItemModel(book.getModel());
            itemModel.update(renderState, stack, itemModelResolver, displayContext, level, entity, seed);
        } else {
            this.original.update(renderState, stack, itemModelResolver, displayContext, level, entity, seed);
        }
    }
}
