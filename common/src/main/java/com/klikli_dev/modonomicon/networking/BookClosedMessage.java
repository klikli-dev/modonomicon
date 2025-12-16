/*
 * SPDX-FileCopyrightText: 2025 klikli-dev
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.util.StreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BookClosedMessage implements Message, CustomPacketPayload {
    public static final Type<BookClosedMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "book_closed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookClosedMessage> STREAM_CODEC = StreamCodec.composite(
            StreamCodecs.enumCodec(InteractionHand.class),
            (m) -> m.hand,
            BookClosedMessage::new
    );

    public InteractionHand hand;

    public BookClosedMessage(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player == null) return;
        ItemStack stack = player.getItemInHand(this.hand);

        //Only set to closed if it is a modonomicon with a valid book.
        //The item in hand could be anything if the book screen was opened e.g. via a custom UI button.
        if (ModonomiconItem.getBook(stack) != null) {
            ModonomiconItem.setBookClosed(stack);
        }
    }
}
