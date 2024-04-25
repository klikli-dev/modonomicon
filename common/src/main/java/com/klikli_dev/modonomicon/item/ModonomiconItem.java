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
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
        return stack.get(DataComponentRegistry.BOOK_ID.get());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var itemInHand = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) {
            if (itemInHand.get(DataComponentRegistry.BOOK_ID.get()) != null) {
                var book = getBook(itemInHand);
                BookGuiManager.get().openBook(book.getId());
            } else {
                Modonomicon.LOG.error("ModonomiconItem: ItemStack has no tag!");
            }
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide);
    }

    @Override
    public Component getName(ItemStack pStack) {
        Book book = getBook(pStack);
        if (book != null) {
            return Component.translatable(book.getName());
        }

        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        Book book = getBook(itemStack);
        if (book != null) {
            if (tooltipFlag.isAdvanced()) {
                list.add(Component.literal("Book ID: ").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal(book.getId().toString()).withStyle(ChatFormatting.RED)));
            }

            if (!book.getTooltip().isBlank()) {
                list.add(Component.translatable(book.getTooltip()).withStyle(ChatFormatting.GRAY));
            }
        } else {
            var compound = new CompoundTag();
            for (var entry : itemStack.getComponents()) {
                var tag = entry.encodeValue(NbtOps.INSTANCE).getOrThrow();
                var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.type());

                compound.put(key.toString(), tag);
            }

            list.add(Component.translatable(Tooltips.ITEM_NO_BOOK_FOUND_FOR_STACK, NbtUtils.toPrettyComponent(compound))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

}
