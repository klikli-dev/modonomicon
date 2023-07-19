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

public class ClickReadAllButtonMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "click_read_all_button");

    public ResourceLocation bookId;
    public boolean readAll; //true to not only read unlocked but even the locked ones

    public ClickReadAllButtonMessage(ResourceLocation bookId, boolean readAll) {
        this.bookId = bookId;
        this.readAll = readAll;
    }

    public ClickReadAllButtonMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.bookId);
        buf.writeBoolean(this.readAll);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.readAll = buf.readBoolean();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book != null) {
            //unlock pages, then update the unlock capability, finally sync.
            var anyRead = false;
            for (var entry : book.getEntries().values()) {
                if ((this.readAll || BookUnlockStateManager.get().isUnlockedFor(player, entry)) && BookUnlockStateManager.get().readFor(player, entry)) {
                    anyRead = true;
                }
            }

            if (anyRead) {
                BookUnlockStateManager.get().updateAndSyncFor(player);
            }
        }
    }
}
