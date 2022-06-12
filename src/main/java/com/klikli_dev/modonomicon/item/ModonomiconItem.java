/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ModonomiconItem extends Item {
    public ModonomiconItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var itemInHand = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) {
            itemInHand.getOrCreateTag().putBoolean(ModonimiconConstants.Nbt.BOOK_OPEN, true);
            BookGuiManager.get().openBook(itemInHand);
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide);
    }
}
