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

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data;
import com.klikli_dev.modonomicon.data.book.BookChapter;
import com.klikli_dev.modonomicon.data.book.BookEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class BookAssetManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MODONOMICON_ASSET_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final BookAssetManager instance = new BookAssetManager();

    //Store chapters by global id
    private final Map<ResourceLocation, BookChapter> chapters = new HashMap<>();

    private boolean loaded;

    private BookAssetManager() {
        super(GSON, FOLDER);
    }

    public static BookAssetManager get() {
        return instance;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Call when both assets and data are loaded to check if each BookEntry has a corresponding BookChapter
     */
    public void linkChaptersToBook(){

        for(var chapter : this.chapters.entrySet()){
            var pathParts = chapter.getKey().getPath().split("/");
            var bookId = new ResourceLocation(chapter.getKey().getNamespace(), pathParts[0]);
            var book = BookDataManager.get().getBook(bookId);
            book.addChapter(chapter.getValue());

            var entry = book.getEntry(chapter.getValue().getEntryId());

            if(entry != null) {
                chapter.getValue().setEntry(entry);
                entry.setChapter(chapter.getValue());
                entry.getCategory().addChapter(chapter.getValue());
            }
        }

        this.verifyChaptersAndEntries();
    }

    public void verifyChaptersAndEntries(){
        for(var book : BookDataManager.get().getBooks().values()){
            //are all entries linked to a chapter?
            book.getEntries().values().forEach(entry -> {
                if(entry.getChapter() == null){
                    Modonomicon.LOGGER.error("Book entry {} in book {} has no corresponding chapter.", entry.getId(), book.getId());
                }
            });

            //are all chapters linked to an entry?
            book.getChapters().values().forEach(chapter -> {
                if(book.getEntry(chapter.getEntryId()) == null){
                    Modonomicon.LOGGER.error("Book chapter {} in book {} has no corresponding entry.", chapter.getId(), book.getId());
                }
            });
        }
    }

    protected void onLoaded() {
        this.loaded = true;
        if (BookDataManager.get().isLoaded()) {
            this.linkChaptersToBook();
        }
    }

    private BookChapter loadChapter(ResourceLocation key, JsonObject value) {
        return BookChapter.fromJson(key, value.getAsJsonObject());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.loaded = false;

        for (var entry : content.entrySet()) {
            var pathParts = entry.getKey().getPath().split("/");

            if (pathParts[1].equals("chapters")) {
                //chapter id skips the book id and the chapters directory
                var localChapterId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
                var chapter = this.loadChapter(localChapterId, entry.getValue().getAsJsonObject());
                //we use global chapter id within the asset manager, but the localized one for use within the book
                this.chapters.put(entry.getKey(), chapter);
            }
        }

        this.onLoaded();
    }
}
