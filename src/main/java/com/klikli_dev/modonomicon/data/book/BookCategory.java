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

package com.klikli_dev.modonomicon.data.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Category;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;

public class BookCategory {
    protected ResourceLocation id;
    protected Book book;
    protected String name;
    protected BookIcon icon;
    protected int sortNumber;
    protected ResourceLocation background;
    protected ResourceLocation entryTextures;
    protected Map<ResourceLocation, BookEntry> entries;
    //TODO: additional backgrounds with custom rendertypes?

    public BookCategory(ResourceLocation id, String name, int sortNumber, BookIcon icon, ResourceLocation background, ResourceLocation entryTextures, Book book) {
        this.id = id;
        this.book = book;
        this.name = name;
        this.sortNumber = sortNumber;
        this.icon = icon;
        this.background = background;
        this.entryTextures = entryTextures;
        this.entries = new HashMap<>();
    }

    public static BookCategory fromJson(ResourceLocation id, JsonObject json, Book book) {
        var name = json.get("name").getAsString();
        var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);
        var icon = BookIcon.fromJson(json.get("icon"));
        var background = new ResourceLocation(GsonHelper.getAsString(json, "background", Category.DEFAULT_BACKGROUND));
        var entryTextures = new ResourceLocation(GsonHelper.getAsString(json, "entry_textures", Category.DEFAULT_ENTRY_TEXTURES));
        return new BookCategory(id, name, sortNumber, icon, background, entryTextures, book);
    }

    public static BookCategory fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Map<ResourceLocation, Book> books) {
        var book = books.get(buffer.readResourceLocation());
        var name = buffer.readUtf();
        var sortNumber = buffer.readInt();
        var icon = BookIcon.fromNetwork(buffer);
        var background = buffer.readResourceLocation();
        var entryTextures = buffer.readResourceLocation();
        return new BookCategory(id, name, sortNumber, icon, background, entryTextures, book);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getName() {
        return this.name;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public ResourceLocation getEntryTextures() {
        return this.entryTextures;
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.book.getId());
        buffer.writeUtf(this.name);
        buffer.writeInt(this.sortNumber);
        this.icon.toNetwork(buffer);
        buffer.writeResourceLocation(this.background);
        buffer.writeResourceLocation(this.entryTextures);
    }
}
