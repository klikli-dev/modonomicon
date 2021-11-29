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
import com.klikli_dev.modonomicon.api.data.book.BookPage;
import com.klikli_dev.modonomicon.registry.BookPageLoaderRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookChapter {
    protected ResourceLocation id;
    protected ResourceLocation entryId;
    protected BookEntry entry;
    protected ResourceLocation categoryId;
    protected BookCategory category;
    protected List<BookPage> pages;

    public BookChapter(ResourceLocation id, ResourceLocation entryId, ResourceLocation categoryId) {
        this.id = id;
        this.entryId = entryId;
        this.categoryId = categoryId;
        this.pages = new ArrayList<>();
    }

    public static BookChapter fromJson(ResourceLocation id, JsonObject json) {
        var entryId = new ResourceLocation(json.get("entry_id").getAsString());
        var categoryId = new ResourceLocation(json.get("category_id").getAsString());

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = json.get("pages").getAsJsonArray();
            for (var pageElem : jsonPages) {
                var page = pageElem.getAsJsonObject();
                var type = new ResourceLocation(page.get("type").getAsString());
                var loader = BookPageLoaderRegistry.getPageLoader(type);
                pages.add(loader.loadPage(page));
            }
        }

        var chapter = new BookChapter(id, entryId, categoryId);
        chapter.setPages(pages);
        return chapter;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public ResourceLocation getEntryId() {
        return this.entryId;
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    public void setEntry(BookEntry entry) {
        this.entry = entry;
    }

    public ResourceLocation getCategoryId() {
        return this.categoryId;
    }

    public List<BookPage> getPages() {
        return this.pages;
    }

    public void setPages(List<BookPage> pages) {
        this.pages = pages;
    }
}
