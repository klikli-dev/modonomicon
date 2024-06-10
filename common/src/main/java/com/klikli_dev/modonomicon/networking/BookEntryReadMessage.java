/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookEntryReadMessage implements Message {

    public static final Type<BookEntryReadMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "book_entry_read"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryReadMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            (m) -> m.bookId,
            ResourceLocation.STREAM_CODEC,
            (m) -> m.entryId,
            BookEntryReadMessage::new
    );

    public ResourceLocation bookId;
    public ResourceLocation entryId;

    public BookEntryReadMessage(ResourceLocation bookId, ResourceLocation entryId) {
        this.bookId = bookId;
        this.entryId = entryId;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var entry = BookDataManager.get().getBook(this.bookId).getEntry(this.entryId);
        //unlock page, then update the unlock capability, finally sync.
        if (BookUnlockStateManager.get().readFor(player, entry)) {
            BookUnlockStateManager.get().updateAndSyncFor(player);
        }
    }
}
