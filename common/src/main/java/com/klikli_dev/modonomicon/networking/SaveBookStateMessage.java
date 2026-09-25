/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SaveBookStateMessage implements Message {

    public static final Type<SaveBookStateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "save_book_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaveBookStateMessage> STREAM_CODEC = CustomPacketPayload.codec(SaveBookStateMessage::encode, SaveBookStateMessage::new);

    public Book book;

    ResourceLocation openCategory = null;

    /**
     * Id kept for safe (re-)resolution on the server: the book may be unknown on the server
     * (loading failed or client/server content mismatch) or not yet built when the packet arrives.
     * See https://github.com/klikli-dev/modonomicon/issues/368
     */
    private ResourceLocation bookId;

    public SaveBookStateMessage(Book book, BookVisualState state) {
        this(book, state.openCategory);
    }

    public SaveBookStateMessage(Book book, ResourceLocation openCategory) {
        this.book = book;
        this.openCategory = openCategory;
        this.bookId = book.getId();
    }

    public SaveBookStateMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(this.book.getId());
        buf.writeBoolean(this.openCategory != null);
        if (this.openCategory != null) {
            buf.writeResourceLocation(this.openCategory);
        }
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        this.bookId = buf.readResourceLocation();
        this.book = BookDataManager.get().getBook(this.bookId);
        if (buf.readBoolean()) {
            this.openCategory = buf.readResourceLocation();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (this.book == null) {
            //The book may not be loaded yet (e.g. right after /reload) - build books lazily and try again.
            BookDataManager.get().tryBuildBooks(player.level());
            this.book = BookDataManager.get().getBook(this.bookId);
        }

        if (this.book == null) {
            //The client knows a book the server does not (book failed to load on the server
            //or client/server content mismatch, e.g. in hybrid modpacks). Never crash the server
            //tick over this, ignore the state save and log.
            Modonomicon.LOG.warn("Received SaveBookStateMessage for unknown book '{}' from player '{}'. The book is not loaded on the server. Ignoring to avoid a crash (see #368).", this.bookId, player.getName().getString());
            return;
        }

        var currentState = BookVisualStateManager.get().getBookStateFor(player, this.book);
        currentState.openCategory = this.openCategory;
        BookVisualStateManager.get().setBookStateFor(player, this.book, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
