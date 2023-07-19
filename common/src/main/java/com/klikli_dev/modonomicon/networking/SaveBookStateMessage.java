/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveBookStateMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "save_book_state");

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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var currentState = BookVisualStateManager.get().getBookStateFor(player, this.book);
        currentState.openCategory = this.openCategory;
        BookVisualStateManager.get().setBookStateFor(player, this.book, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
