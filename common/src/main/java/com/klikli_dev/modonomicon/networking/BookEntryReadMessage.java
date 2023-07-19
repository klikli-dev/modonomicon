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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookEntryReadMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "book_entry_read");

    public ResourceLocation bookId;
    public ResourceLocation entryId;

    public BookEntryReadMessage(ResourceLocation bookId, ResourceLocation entryId) {
        this.bookId = bookId;
        this.entryId = entryId;
    }

    public BookEntryReadMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.bookId);
        buf.writeResourceLocation(this.entryId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.entryId = buf.readResourceLocation();
    }


    @Override
    public ResourceLocation getId() {
        return ID;
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
