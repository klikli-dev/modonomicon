/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SyncBookDataMessage implements Message {


    public static final Type<SyncBookDataMessage> TYPE = new Type<>(new ResourceLocation(Modonomicon.MOD_ID, "sync_book_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBookDataMessage> STREAM_CODEC = CustomPacketPayload.codec(SyncBookDataMessage::encode, SyncBookDataMessage::new);

    public ConcurrentMap<ResourceLocation, Book> books = new ConcurrentHashMap<>();

    public SyncBookDataMessage(ConcurrentMap<ResourceLocation, Book> books) {
        this.books = books;
    }

    public SyncBookDataMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.books.size());
        for (var book : this.books.values()) {
            buf.writeResourceLocation(book.getId());
            book.toNetwork(buf);

            buf.writeVarInt(book.getCategories().size());
            for (var category : book.getCategories().values()) {
                buf.writeResourceLocation(category.getId());
                category.toNetwork(buf);

                buf.writeVarInt(category.getEntries().size());
                for (var entry : category.getEntries().values()) {
                    buf.writeResourceLocation(entry.getId());
                    entry.toNetwork(buf);
                }
            }

            buf.writeVarInt(book.getCommands().size());
            for (var command : book.getCommands().values()) {
                buf.writeResourceLocation(command.getId());
                command.toNetwork(buf);
            }
        }
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        //build books
        int bookCount = buf.readVarInt();
        for (int i = 0; i < bookCount; i++) {
            ResourceLocation bookId = buf.readResourceLocation();
            Book book = Book.fromNetwork(bookId, buf);
            this.books.put(bookId, book);

            int categoryCount = buf.readVarInt();
            for (int j = 0; j < categoryCount; j++) {
                ResourceLocation categoryId = buf.readResourceLocation();
                BookCategory category = BookCategory.fromNetwork(categoryId, buf);

                //link category and book
                book.addCategory(category);

                int entryCount = buf.readVarInt();
                for (int k = 0; k < entryCount; k++) {
                    ResourceLocation entryId = buf.readResourceLocation();
                    BookEntry bookEntry = BookEntry.fromNetwork(entryId, buf);

                    //link entry and category
                    category.addEntry(bookEntry);
                }
            }

            int commandCount = buf.readVarInt();
            for (int j = 0; j < commandCount; j++) {
                ResourceLocation commandId = buf.readResourceLocation();
                BookCommand command = BookCommand.fromNetwork(commandId, buf);

                //link command and book
                book.addCommand(command);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookDataManager.get().onDatapackSyncPacket(this);
    }
}
