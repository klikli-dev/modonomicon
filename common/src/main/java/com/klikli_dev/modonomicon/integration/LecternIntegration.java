// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.networking.OpenBookOnClientMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LecternIntegration {

    public static InteractionResult rightClick(Player player, Level pLevel, InteractionHand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = pLevel.getBlockState(pos);

        if (!(pLevel.getBlockEntity(pos) instanceof LecternBlockEntity lectern))
            return InteractionResult.PASS;

        if (state.getValue(LecternBlock.HAS_BOOK)) {
            if (player.isShiftKeyDown()) {
                takeBook(player, lectern);
            } else {
                if (!pLevel.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    if (openBook(serverPlayer, lectern.getBook()))
                        return InteractionResult.SUCCESS;
                }
            }
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.get(DataComponentRegistry.BOOK_ID.get()) != null) {
                if (LecternBlock.tryPlaceBook(player, pLevel, pos, state, stack)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    private static boolean openBook(ServerPlayer player, ItemStack stack) {
        var bookId = stack.get(DataComponentRegistry.BOOK_ID.get());
        if (bookId != null) {
            Services.NETWORK.sendTo(player, new OpenBookOnClientMessage(bookId));
            return true;
        }
        return false;
    }

    private static void takeBook(Player player, LecternBlockEntity lectern) {
        ItemStack stack = lectern.getBook();
        var bookId = stack.get(DataComponentRegistry.BOOK_ID.get());
        if (bookId != null) {
            lectern.setBook(ItemStack.EMPTY);
            LecternBlock.resetBookState(player, lectern.getLevel(), lectern.getBlockPos(), lectern.getBlockState(), false);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
