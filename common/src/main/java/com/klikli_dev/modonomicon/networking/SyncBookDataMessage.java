/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.entries.*;
import com.klikli_dev.modonomicon.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SyncBookDataMessage implements Message {
    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "sync_book_data");
    public ConcurrentMap<ResourceLocation, Book> books = new ConcurrentHashMap<>();

    public SyncBookDataMessage(ConcurrentMap<ResourceLocation, Book> books) {
        this.books = books;
    }

    public SyncBookDataMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
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
                    buf.writeResourceLocation(entry.getType());
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

    @Override
    public void decode(FriendlyByteBuf buf) {
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
                    ResourceLocation entryTypeId = buf.readResourceLocation();
                    BookEntry entry = LoaderRegistry.getEntryNetworkLoader(entryTypeId).fromNetwork(buf);

                    //link entry and category
                    category.addEntry(entry);
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
    public ResourceLocation getId() {
        return ID;
    }


    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookDataManager.get().onDatapackSyncPacket(this);
    }
}
