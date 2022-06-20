/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Message;
import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class BookEntryReadMessage implements Message {

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
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
            var entry = BookDataManager.get().getBook(this.bookId).getEntry(this.entryId);
            //unlock page, then update the unlock capability, finally sync.
            if (capability.read(entry)) {
                capability.update(player);
                capability.sync(player);
            }
        });
    }
}
