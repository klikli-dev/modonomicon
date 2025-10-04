/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Nbt;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ModonomiconItem extends Item {
    public ModonomiconItem(Properties pProperties) {
        super(pProperties);
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

    /**
     * Sets the book state to closed in NBT. Should be called server-side.
     */
    public static void setBookClosed(ItemStack stack) {
        if (stack != null && stack.hasTag()) {
            stack.getTag().putString(Nbt.ITEM_BOOK_OPEN_STATE_TAG, "closed");
        }
    }

    /**
     * Returns true if the book is open, false if closed or not set.
     */
    public static boolean isBookOpen(ItemStack stack) {
        if (stack != null && stack.hasTag() && stack.getTag().contains(Nbt.ITEM_BOOK_OPEN_STATE_TAG)) {
            return "open".equals(stack.getTag().getString(Nbt.ITEM_BOOK_OPEN_STATE_TAG));
        }
        return false;
    }

    public Book getBookFor(ItemStack stack) {
        return getBook(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var itemInHand = pPlayer.getItemInHand(pUsedHand);

        // Set the book state to open in NBT
        if (!itemInHand.hasTag()) {
            itemInHand.getOrCreateTag();
        }
        itemInHand.getTag().putString(Nbt.ITEM_BOOK_OPEN_STATE_TAG, "open");

        if (pLevel.isClientSide) {
            if (itemInHand.hasTag()) {
                var book = this.getBookFor(itemInHand);
                BookGuiManager.get().openBook(book.getId());
            } else {
                Modonomicon.LOG.error("ModonomiconItem: ItemStack has no tag!");
            }
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide);
    }

    @Override
    public Component getName(ItemStack pStack) {
        Book book = this.getBookFor(pStack);
        if (book != null) {
            return Component.translatable(book.getName());
        }

        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        Book book = this.getBookFor(stack);
        if (book != null) {
            if (flagIn.isAdvanced()) {
                tooltip.add(Component.literal("Book ID: ").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal(book.getId().toString()).withStyle(ChatFormatting.RED)));
            }

            if (!book.getTooltip().isBlank()) {
                tooltip.add(Component.translatable(book.getTooltip()).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable(Tooltips.ITEM_NO_BOOK_FOUND_FOR_STACK,
                            !stack.hasTag() ? Component.literal("{}") : NbtUtils.toPrettyComponent(stack.getTag()))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
