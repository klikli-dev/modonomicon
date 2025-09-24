/*
 * SPDX-FileCopyrightText: 2025 klikli-dev
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.world.item.ItemStack;

public class BookClosedMessage implements Message {
    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "book_closed");

    public InteractionHand hand;

    public BookClosedMessage(InteractionHand hand) {
        this.hand = hand;
    }

    public BookClosedMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player == null) return;
        ItemStack stack = player.getItemInHand(this.hand);
        if (stack != null && stack.getItem() instanceof ModonomiconItem) {
            ModonomiconItem.setBookClosed(stack);
        }
    }
}
