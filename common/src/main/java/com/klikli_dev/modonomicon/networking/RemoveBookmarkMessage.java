/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RemoveBookmarkMessage implements Message {

    public static final Type<RemoveBookmarkMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "remove_bookmark"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveBookmarkMessage> STREAM_CODEC = StreamCodec.composite(
            BookAddress.STREAM_CODEC,
            (m) -> m.address,
            RemoveBookmarkMessage::new
    );


    public BookAddress address;

    public RemoveBookmarkMessage(BookAddress address) {
        this.address = address;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.address.bookId());
        BookVisualStateManager.get().removeBookmarkFor(player, book, this.address);
        BookVisualStateManager.get().syncFor(player);
    }
}
