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
import io.netty.buffer.ByteBuf;
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

    public SaveBookStateMessage(Book book, BookVisualState state) {
        this(book, state.openCategory);
    }

    public SaveBookStateMessage(Book book, ResourceLocation openCategory) {
        this.book = book;
        this.openCategory = openCategory;
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
        this.book = BookDataManager.get().getBook(buf.readResourceLocation());
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
        var currentState = BookVisualStateManager.get().getBookStateFor(player, this.book);
        currentState.openCategory = this.openCategory;
        BookVisualStateManager.get().setBookStateFor(player, this.book, currentState);
        BookVisualStateManager.get().syncFor(player);
    }
}
