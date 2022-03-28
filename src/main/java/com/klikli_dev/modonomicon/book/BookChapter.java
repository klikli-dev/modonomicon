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
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.registry.BookPageLoaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BookChapter {
    protected ResourceLocation id;
    protected ResourceLocation entryId;
    protected BookEntry entry;
    protected List<BookPage> pages;

    public BookChapter(ResourceLocation id, ResourceLocation entryId) {
        this.id = id;
        this.entryId = entryId;
        this.pages = new ArrayList<>();
    }

    public static BookChapter fromJson(ResourceLocation id, JsonObject json) {

        var entryId = new ResourceLocation( GsonHelper.getAsString(json, "entry"));

        var pages = new ArrayList<BookPage>();
        if (json.has("pages")) {
            var jsonPages = GsonHelper.getAsJsonArray(json, "pages");
            for (var pageElem : jsonPages) {
                var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
                var loader = BookPageLoaderRegistry.getPageLoader(type);
                var page = loader.fromJson(pageJson);
                pages.add(page);
            }
        }

        var chapter = new BookChapter(id, entryId);
        chapter.setPages(pages);

        int pageNum = 0;
        for(var page : pages){
            page.setChapter(chapter);
            page.setPageNumber(pageNum++);
        }

        return chapter;
    }

    public ResourceLocation getId() {
        return this.id;
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

    public List<BookPage> getPages() {
        return this.pages;
    }

    public void setPages(List<BookPage> pages) {
        this.pages = pages;
    }
}
