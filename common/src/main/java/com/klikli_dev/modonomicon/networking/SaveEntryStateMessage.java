/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.entries.*;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveEntryStateMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "save_entry_state");

    public BookEntry entry;
    public int openPagesIndex;

    public SaveEntryStateMessage(ContentBookEntry entry, int openPagesIndex) {
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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var currentState = BookVisualStateManager.get().getEntryStateFor(player, this.entry);
        currentState.openPagesIndex = this.openPagesIndex;
        BookVisualStateManager.get().setEntryStateFor(player, this.entry, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
