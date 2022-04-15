/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Category;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;

public class BookCategory {
    protected ResourceLocation id;
    protected ResourceLocation bookId;
    protected Book book;
    protected String name;
    protected BookIcon icon;
    protected int sortNumber;
    protected ResourceLocation background;
    protected ResourceLocation entryTextures;
    protected Map<ResourceLocation, BookEntry> entries;
    //TODO: additional backgrounds with custom rendertypes?

    public BookCategory(ResourceLocation id, ResourceLocation bookId, String name, int sortNumber, BookIcon icon, ResourceLocation background, ResourceLocation entryTextures) {
        this.id = id;
        this.bookId = bookId;
        this.name = name;
        this.sortNumber = sortNumber;
        this.icon = icon;
        this.background = background;
        this.entryTextures = entryTextures;
        this.entries = new HashMap<>();
    }

    public static BookCategory fromJson(ResourceLocation id, JsonObject json, ResourceLocation bookId) {
        var name = GsonHelper.getAsString(json, "name");
        var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);
        var icon = BookIcon.fromString(GsonHelper.getAsString(json, "icon"));
        var background = new ResourceLocation(GsonHelper.getAsString(json, "background", Category.DEFAULT_BACKGROUND));
        var entryTextures = new ResourceLocation(GsonHelper.getAsString(json, "entry_textures", Category.DEFAULT_ENTRY_TEXTURES));
        return new BookCategory(id, bookId, name, sortNumber, icon, background, entryTextures);
    }

    public static BookCategory fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var bookId = buffer.readResourceLocation();
        var name = buffer.readUtf();
        var sortNumber = buffer.readInt();
        var icon = BookIcon.fromNetwork(buffer);
        var background = buffer.readResourceLocation();
        var entryTextures = buffer.readResourceLocation();
        return new BookCategory(id, bookId, name, sortNumber, icon, background, entryTextures);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Book book) {
        this.book = book;

        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.build(this);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().entryId = null;
        }
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

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.book.getId());
        buffer.writeUtf(this.name);
        buffer.writeInt(this.sortNumber);
        this.icon.toNetwork(buffer);
        buffer.writeResourceLocation(this.background);
        buffer.writeResourceLocation(this.entryTextures);
    }

    public ResourceLocation getBookId() {
        return this.bookId;
    }
}
