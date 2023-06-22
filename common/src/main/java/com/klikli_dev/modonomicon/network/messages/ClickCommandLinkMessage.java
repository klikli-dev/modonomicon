/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClickCommandLinkMessage implements Message {

    public ResourceLocation bookId;
    public ResourceLocation commandId;

    public ClickCommandLinkMessage(ResourceLocation bookId, ResourceLocation commandId) {
        this.bookId = bookId;
        this.commandId = commandId;
    }

    public ClickCommandLinkMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.bookId);
        buf.writeResourceLocation(this.commandId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.commandId = buf.readResourceLocation();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book != null) {
            var command = book.getCommand(this.commandId);
            if (command != null) {
                command.execute(player);
            }
        }
    }
}
