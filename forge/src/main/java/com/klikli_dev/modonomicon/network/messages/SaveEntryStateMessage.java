/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class SaveEntryStateMessage implements Message {

    public BookEntry entry;
    public int openPagesIndex;

    public SaveEntryStateMessage(BookEntry entry, int openPagesIndex) {
        this.entry = entry;
        this.openPagesIndex = openPagesIndex;
    }

    public SaveEntryStateMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.entry.getBook().getId());
        buf.writeResourceLocation(this.entry.getId());
        buf.writeVarInt(this.openPagesIndex);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.entry = BookDataManager.get().getBook(buf.readResourceLocation()).getEntry(buf.readResourceLocation());
        this.openPagesIndex = buf.readVarInt();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        var currentState = BookStateCapability.getEntryStateFor(player, this.entry);
        currentState.openPagesIndex = this.openPagesIndex;
        BookStateCapability.setEntryStateFor(player, this.entry, currentState);
        BookStateCapability.syncFor(player);
    }
}
