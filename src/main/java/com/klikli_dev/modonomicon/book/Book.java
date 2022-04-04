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

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book {
    protected ResourceLocation id;
    protected String name;
    protected ResourceLocation bookOverviewTexture;
    protected ResourceLocation bookContentTexture;
    protected ResourceLocation turnPageSound;
    protected Map<ResourceLocation, BookCategory> categories;
    protected Map<ResourceLocation, BookEntry> entries;
    protected int defaultTitleColor;
    public Book(ResourceLocation id, String name, ResourceLocation bookOverviewTexture, ResourceLocation bookContentTexture, ResourceLocation turnPageSound, int defaultTitleColor) {
        this.id = id;
        this.name = name;
        this.bookOverviewTexture = bookOverviewTexture;
        this.bookContentTexture = bookContentTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
        this.categories = new HashMap<>();
        this.entries = new HashMap<>();
    }

    public static Book fromJson(ResourceLocation id, JsonObject json) {
        var name = GsonHelper.getAsString(json, "name");
        var bookOverviewTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_overview_texture", Data.Book.DEFAULT_OVERVIEW_TEXTURE));
        var bookContentTexture = new ResourceLocation(GsonHelper.getAsString(json, "book_content_texture", Data.Book.DEFAULT_CONTENT_TEXTURE));
        var turnPageSound = new ResourceLocation(GsonHelper.getAsString(json, "turn_page_sound", Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = GsonHelper.getAsInt(json, "defaultTitleColor", 0x00000);
        return new Book(id, name, bookOverviewTexture, bookContentTexture, turnPageSound, defaultTitleColor);
    }

    //TODO: further properties for customization, such as book item, title color...

    public static Book fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var bookOverviewTexture = buffer.readResourceLocation();
        var bookContentTexture = buffer.readResourceLocation();
        var turnPageSound = buffer.readResourceLocation();
        var defaultTitleColor = buffer.readInt();
        return new Book(id, name, bookOverviewTexture, bookContentTexture, turnPageSound, defaultTitleColor);
    }

    public ResourceLocation getTurnPageSound() {
        return this.turnPageSound;
    }

    public void build() {
        //first "backlink" all our entries directly into the book
        for (var category : this.categories.values()) {
            for (var entry : category.getEntries().values()) {
                this.addEntry(entry);
            }
        }

        //then build categories, which will in turn build entries (which need the above backlinks to resolve parents)
        for (var category : this.categories.values()) {
            category.build(this);
        }
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public void addCategory(BookCategory category) {
        this.categories.putIfAbsent(category.id, category);
    }

    public BookCategory getCategory(ResourceLocation id) {
        return this.categories.get(id);
    }

    public Map<ResourceLocation, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted() {
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeResourceLocation(this.bookOverviewTexture);
        buffer.writeResourceLocation(this.bookContentTexture);
        buffer.writeInt(this.defaultTitleColor);
    }

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }
}
