/*
 * LGPL-3-0
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.registry.BookPageLoaderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BookEntry {
    protected ResourceLocation id;
    protected ResourceLocation categoryId;
    protected BookCategory category;
    protected List<BookEntryParent> parents;
    protected String name;
    protected String description;
    protected BookIcon icon;
    protected int x;
    protected int y;
    protected List<BookPage> pages;

    //TODO: entry type for background texture

    public BookEntry(ResourceLocation id, ResourceLocation categoryId, String name, String description, BookIcon icon, int x, int y, List<BookEntryParent> parents) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.parents = parents;
    }

    public static BookEntry fromJson(ResourceLocation id, JsonObject json) {
        var categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category"));
        var name = GsonHelper.getAsString(json, "name");
        var description = GsonHelper.getAsString(json, "description", "");
        var icon =BookIcon.fromString(GsonHelper.getAsString(json, "icon"));
        var x = GsonHelper.getAsInt(json, "x");
        var y = GsonHelper.getAsInt(json, "y");

        var parentEntries = new ArrayList<BookEntryParent>();

        if (json.has("parents")) {
            JsonArray parents = GsonHelper.getAsJsonArray(json, "parents");
            for (var parent : parents) {
                parentEntries.add(BookEntryParent.fromJson(parent.getAsJsonObject()));
            }
        }

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = GsonHelper.getAsJsonArray(json, "pages");
            for (var pageElem : jsonPages) {
                var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
                var loader = BookPageLoaderRegistry.getPageJsonLoader(type);
                var page = loader.fromJson(pageJson);
                pages.add(page);
            }
        }

        var entry =  new BookEntry(id, categoryId, name, description, icon, x, y, parentEntries);
        entry.setPages(pages);
        return entry;
    }

    public static BookEntry fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var categoryId = buffer.readResourceLocation();
        var name = buffer.readUtf();
        var description = buffer.readUtf();
        var icon = BookIcon.fromNetwork(buffer);
        var x = buffer.readVarInt();
        var y = buffer.readVarInt();

        var parentEntries = new ArrayList<BookEntryParent>();

        var parentCount = buffer.readVarInt();
        for (var i = 0; i < parentCount; i++) {
            parentEntries.add(BookEntryParent.fromNetwork(buffer));
        }

        var pages = new ArrayList<BookPage>();
        var pageCount = buffer.readVarInt();
        for (var i = 0; i < pageCount; i++) {
            var type = buffer.readResourceLocation();
            var loader = BookPageLoaderRegistry.getPageNetworkLoader(type);
            var page = loader.fromNetwork(buffer);
            pages.add(page);
        }

        var entry =  new BookEntry(id, categoryId, name, description, icon, x, y, parentEntries);
        entry.setPages(pages);
        return entry;
    }


    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.categoryId);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.description);
        this.icon.toNetwork(buffer);
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeVarInt(this.parents.size());
        for (var parent : this.parents) {
            parent.toNetwork(buffer);
        }

        buffer.writeVarInt(this.pages.size());
        for(var page : this.pages){
            buffer.writeResourceLocation(page.getType());
            page.toNetwork(buffer);
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getCategoryId() {
        return this.categoryId;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public List<BookEntryParent> getParents() {
        return this.parents;
    }

    public void setParents(List<BookEntryParent> parents) {
        this.parents = parents;
    }

    public String getName() {
        return this.name;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public List<BookPage> getPages() {
        return this.pages;
    }

    public void setPages(List<BookPage> pages) {
        this.pages = pages;

        int pageNum = 0;
        for(var page : pages){
            page.setParentEntry(this);
            page.setPageNumber(pageNum++);
        }
    }
}
