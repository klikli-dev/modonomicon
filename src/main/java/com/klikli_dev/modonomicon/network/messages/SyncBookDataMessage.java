/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.book.*;
import com.klikli_dev.modonomicon.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncBookDataMessage implements IMessage {

    private Map<ResourceLocation, Book> books = new HashMap<>();
    private Map<ResourceLocation, BookCategory> categories = new HashMap<>();
    private Map<ResourceLocation, BookEntry> entries = new HashMap<>();

    public SyncBookDataMessage(Map<ResourceLocation, Book> books,
                               Map<ResourceLocation, BookCategory> categories,
                               Map<ResourceLocation, BookEntry> entries) {
        this.books = books;
        this.categories = categories;
        this.entries = entries;
    }

    public SyncBookDataMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.books.size());
        for (Map.Entry<ResourceLocation, Book> entry : this.books.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().toNetwork(buf);
        }

        buf.writeInt(this.categories.size());
        for (Map.Entry<ResourceLocation, BookCategory> entry : this.categories.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().toNetwork(buf);
        }

        buf.writeInt(this.entries.size());
        for (Map.Entry<ResourceLocation, BookEntry> entry : this.entries.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().toNetwork(buf);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        //build books
        int bookCount = buf.readInt();
        for (int i = 0; i < bookCount; i++) {
            ResourceLocation key = buf.readResourceLocation();
            Book book = Book.fromNetwork(key, buf);
            this.books.put(key, book);
        }

        //build categories
        int categoryCount = buf.readInt();
        for (int i = 0; i < categoryCount; i++) {
            ResourceLocation key = buf.readResourceLocation();
            BookCategory category = BookCategory.fromNetwork(key, buf, this.books);
            this.categories.put(key, category);
            category.getBook().getCategories().put(category.getId(), category);
        }

        //build entries
        int entryCount = buf.readInt();
        for (int i = 0; i < entryCount; i++) {
            ResourceLocation key = buf.readResourceLocation();
            BookEntry entry = BookEntry.fromNetwork(key, buf, this.categories);
            this.entries.put(key, entry);
            entry.getCategory().getEntries().put(entry.getId(), entry);
        }

        //resolve entry parents
        BookDataManager.resolveBookEntryParents(this.entries);
    }



    @Override
    public void onClientReceived(Minecraft minecraft, Player player, Context context) {
        IMessage.super.onClientReceived(minecraft, player, context);
    }
}
