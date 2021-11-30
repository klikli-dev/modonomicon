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

import java.util.HashMap;
import java.util.Map;


public class BookAssetManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MODONOMICON_ASSET_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final BookAssetManager instance = new BookAssetManager();

    private final Map<ResourceLocation, BookChapter> chapters = new HashMap<>();
    private final Map<ResourceLocation, BookChapter> entriesToChapters = new HashMap<>();

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

    public BookChapter getChapter(ResourceLocation id) {
        return this.chapters.get(id);
    }

    public BookChapter getChapterForEntryId(ResourceLocation entryId) {
        return this.entriesToChapters.get(entryId);
    }

    public BookChapter getChapterForEntry(BookEntry entry) {
        return this.getChapterForEntryId(entry.getId());
    }

    /**
     * Call when both assets and data are loaded to check if each BookEntry has a corresponding BookChapter
     */
    public void verifyChapters() {
        BookDataManager.get().getEntries().values().forEach(entry -> {
            if (!this.entriesToChapters.containsKey(entry.getId())) {
                Modonomicon.LOGGER.error("Book entry {} has no corresponding chapter.", entry.getId());
            }
        } );
    }

    protected void onLoaded() {
        this.loaded = true;
        if (BookDataManager.get().isLoaded()) {
            this.verifyChapters();
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
                //chapters use their global ID, including namespace
                var chapter = this.loadChapter(entry.getKey(), entry.getValue().getAsJsonObject());
                this.chapters.put(entry.getKey(), chapter);
                this.entriesToChapters.put(chapter.getEntryId(), chapter);
            }
        }

        this.onLoaded();
    }
}
