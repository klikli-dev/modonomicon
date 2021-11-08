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
import com.klikli_dev.modonomicon.data.book.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class BookDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MODONOMICON_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final BookDataManager instance = new BookDataManager();

    private final Map<ResourceLocation, Book> books = new HashMap<>();
    private final Map<ResourceLocation, BookCategory> categories = new HashMap<>();
    private final Map<ResourceLocation, BookEntry> entries = new HashMap<>();
    private boolean loaded;

    private BookDataManager() {
        super(GSON, FOLDER);
    }

    public static BookDataManager get() {
        return instance;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public Map<ResourceLocation, Book> getBooks() {
        return this.books;
    }

    public Book getBook(ResourceLocation id) {
        return this.books.get(id);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    protected void onLoaded() {
        this.loaded = true;
    }

    private Book loadBook(ResourceLocation key, JsonObject value) {
        return Book.fromJson(key, value.getAsJsonObject());
    }

    private BookCategory loadCategory(ResourceLocation key, JsonObject value) {
        return BookCategory.fromJson(key, value.getAsJsonObject());
    }

    private BookEntry loadEntry(ResourceLocation key, JsonObject value, Map<ResourceLocation, BookCategory> categories) {
        return BookEntry.fromJson(key, value.getAsJsonObject(), categories);
    }

    private void categorizeContent(Map<ResourceLocation, JsonElement> content,
                                   HashMap<ResourceLocation, JsonObject> bookJsons,
                                   HashMap<ResourceLocation, JsonObject> categoryJsons,
                                   HashMap<ResourceLocation, JsonObject> entryJsons) {
        for (var entry : content.entrySet()) {
            var pathParts = entry.getKey().getPath().split("/");

            var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
            switch (pathParts[1]) {
                case "book" -> {
                    bookJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "entries" -> {
                    entryJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "categories" -> {
                    categoryJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                default -> {
                    Modonomicon.LOGGER.warn("Found unknown content for book '{}': '{}'. Should be one of: [File: book.json, Directory: entries/, Directory: categories/]", bookId, entry.getKey());
                }
            }
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.loaded = false;
        this.books.clear();
        this.categories.clear();
        this.entries.clear();

        //first, load all json entries
        var bookJsons = new HashMap<ResourceLocation, JsonObject>();
        var categoryJsons = new HashMap<ResourceLocation, JsonObject>();
        var entryJsons = new HashMap<ResourceLocation, JsonObject>();
        this.categorizeContent(content, bookJsons, categoryJsons, entryJsons);

        //build books
        for (var entry : bookJsons.entrySet()) {
            var pathParts = entry.getKey().getPath().split("/");
            var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
            var book = this.loadBook(bookId, entry.getValue());
            this.books.put(book.getId(), book);
        }

        //build categories
        for (var entry : categoryJsons.entrySet()) {
            //load categories and link to book
            var pathParts = entry.getKey().getPath().split("/");
            var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
            //category id skips the book id and the category directory
            var categoryId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
            var category = this.loadCategory(categoryId, entry.getValue());
            this.categories.put(category.getId(), category);

            this.books.get(bookId).getCategories().put(category.getId(), category);
        }

        //build entries
        for (var entry : entryJsons.entrySet()) {
            //load entries and link to category
            var pathParts = entry.getKey().getPath().split("/");
            var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
            //entry id skips the book id and the entries directory
            var entryId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
            var bookEntry = this.loadEntry(entryId, entry.getValue(), this.books.get(bookId).getCategories());
            this.entries.put(bookEntry.getId(), bookEntry);

            bookEntry.getCategory().addEntry(bookEntry);
        }

        //resolve entry parents
        for (var entry : this.entries.values()) {
            var newParents = new ArrayList<BookEntryParent>();

            for (var parent : entry.getParents()) {
                newParents.add(new ResolvedBookEntryParent(this.entries.get(parent.getEntryId())));
            }

            entry.setParents(newParents);
        }

        this.onLoaded();
    }
}
