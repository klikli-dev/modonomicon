/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class SaveBookStateMessage implements Message {

    public Book book;

    ResourceLocation openCategory = null;

    public SaveBookStateMessage(Book book, ResourceLocation openCategory) {
        this.book = book;
        this.openCategory = openCategory;
    }

    public SaveBookStateMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.book.getId());
        buf.writeBoolean(this.openCategory != null);
        if (this.openCategory != null) {
            buf.writeResourceLocation(this.openCategory);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.book = BookDataManager.get().getBook(buf.readResourceLocation());
        if (buf.readBoolean()) {
            this.openCategory = buf.readResourceLocation();
        }
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, Context context) {
        var currentState = BookStateCapability.getBookStateFor(player, this.book);
        currentState.openCategory = this.openCategory;
        BookStateCapability.setBookStateFor(player, this.book, currentState);
        BookStateCapability.syncFor(player);
    }
}
