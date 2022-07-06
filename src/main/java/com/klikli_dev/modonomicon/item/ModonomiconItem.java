/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Nbt;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
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

            if(itemInHand.hasTag()){
                var id = getBookId(itemInHand);
                BookGuiManager.get().openBook(id);
            } else {
                Modonomicon.LOGGER.error("ModonomiconItem: ItemStack has no tag!");
            }
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        String tabName = tab.getRecipeFolderName();
        BookDataManager.get().getBooks().values().forEach(b -> {
            if (tab == CreativeModeTab.TAB_SEARCH || b.getCreativeTab().equals(tabName)) {

                ItemStack stack = new ItemStack(ItemRegistry.MODONOMICON.get());

                CompoundTag cmp = new CompoundTag();
                cmp.putString(Nbt.ITEM_BOOK_ID_TAG, b.getId().toString());
                stack.setTag(cmp);

                items.add(stack);
            }
        });
    }

    public static Book getBook(ItemStack stack) {
        ResourceLocation res = getBookId(stack);
        if (res == null) {
            return null;
        }
        return BookDataManager.get().getBook(res);
    }

    private static ResourceLocation getBookId(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(Nbt.ITEM_BOOK_ID_TAG)) {
            return null;
        }

        String bookStr = stack.getTag().getString(Nbt.ITEM_BOOK_ID_TAG);
        return ResourceLocation.tryParse(bookStr);
    }
}
