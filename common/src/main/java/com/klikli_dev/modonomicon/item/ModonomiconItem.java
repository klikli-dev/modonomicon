/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ModonomiconItem extends Item {
    public ModonomiconItem(Properties pProperties) {
        super(pProperties);
    }

    public static Book getBook(ItemStack stack) {
        Identifier res = getBookId(stack);
        if (res == null) {
            return null;
        }
        return BookDataManager.get().getBook(res);
    }

    public static Identifier getBookId(ItemStack stack) {
        return stack.get(DataComponentRegistry.BOOK_ID.get());
    }

    /**
     * Sets the book state to closed using data components. Should be called server-side.
     */
    public static void setBookClosed(ItemStack stack) {
        if (stack != null) {
            stack.set(DataComponentRegistry.BOOK_OPEN.get(), false);
        }
    }

    /**
     * Returns true if the book is open, false if closed or not set.
     */
    public static boolean isBookOpen(ItemStack stack) {
        if (stack != null && stack.has(DataComponentRegistry.BOOK_OPEN.get())) {
            Boolean val = stack.get(DataComponentRegistry.BOOK_OPEN.get());
            return Boolean.TRUE.equals(val);
        }
        return false;
    }

    public Book getBookFor(ItemStack stack) {
        return getBook(stack);
    }

    @Override
    public @NotNull InteractionResult use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var itemInHand = pPlayer.getItemInHand(pUsedHand);

        // Set the book state to open using data components
        itemInHand.set(DataComponentRegistry.BOOK_OPEN.get(), true);

        if (pLevel.isClientSide()) {
            var book = this.getBookFor(itemInHand);
            if (book != null) {
                BookGuiManager.get().openBook(BookAddress.defaultFor(book));
            } else {
                Modonomicon.LOG.error("ModonomiconItem: ItemStack has no tag!");
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack pStack) {
        Book book = this.getBookFor(pStack);
        if (book != null) {
            return Component.translatable(book.getName());
        }

        return super.getName(pStack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext tooltipContext, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> consumer, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);

        Book book = this.getBookFor(itemStack);
        if (book != null) {
            if (tooltipFlag.isAdvanced()) {
                consumer.accept(Component.literal("Book ID: ").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal(book.getId().toString()).withStyle(ChatFormatting.RED)));
            }

            if (!book.getTooltip().isBlank()) {
                consumer.accept(Component.translatable(book.getTooltip()).withStyle(ChatFormatting.GRAY));
            }
        } else {
            var compound = new CompoundTag();
            for (var entry : itemStack.getComponents()) {
                var tag = entry.encodeValue(NbtOps.INSTANCE).getOrThrow();
                var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.type());

                compound.put(key.toString(), tag);
            }

            consumer.accept(Component.translatable(Tooltips.ITEM_NO_BOOK_FOUND_FOR_STACK, NbtUtils.toPrettyComponent(compound))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
