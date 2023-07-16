/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.networking.SyncBookDataMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class BookDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MODONOMICON_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final BookDataManager instance = new BookDataManager();

    private ConcurrentMap<ResourceLocation, Book> books = new ConcurrentHashMap<>();
    private boolean loaded;
    private boolean booksBuilt;

    private BookDataManager() {
        super(GSON, FOLDER);
    }

    public static BookDataManager get() {
        return instance;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public ConcurrentMap<ResourceLocation, Book> getBooks() {
        return this.books;
    }

    public Book getBook(ResourceLocation id) {
        return this.books.get(id);
    }

    public Message getSyncMessage() {
        return new SyncBookDataMessage(this.books);
    }

    public boolean areBooksBuilt() {
        return this.booksBuilt;
    }

    public void onDatapackSyncPacket(SyncBookDataMessage message) {
        this.preLoad();
        this.books = message.books;
        this.onLoadingComplete();
    }

    public void onDatapackSync(ServerPlayer player) {

        this.tryBuildBooks(); //lazily build books when first client connects

        Message syncMessage = this.getSyncMessage();

        Services.NETWORK.sendToSplit(player, syncMessage);
        //TODO: Check if we need to send to player list here
    }

    public void onRecipesUpdated() {
        this.tryBuildBooks();
        this.prerenderMarkdown();
    }

    public void preLoad() {
        this.booksBuilt = false;
        this.loaded = false;
        this.books.clear();
        BookErrorManager.get().reset();
    }

    public void buildBooks() {
        for (var book : this.books.values()) {
            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());
            try {
                book.build();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to build book '" + book.getId() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }
    }

    public void prerenderMarkdown() {
        //TODO: allow modders to configure this renderer

        Modonomicon.LOG.info("Pre-rendering markdown ...");
        for (var book : this.books.values()) {
            var textRenderer = new BookTextRenderer(book);
            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());
            try {
                book.prerenderMarkdown(textRenderer);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to render markdown for book '" + book.getId() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }
        Modonomicon.LOG.info("Finished pre-rendering markdown.");
    }

    public void addReadConditions() {
        for (var book : this.books.values()) {
            if (book.autoAddReadConditions()) {
                for (var entry : book.getEntries().values()) {
                    if (entry.getCondition().getType().equals(Condition.NONE)) {
                        if (entry.getParents().size() == 1) {
                            entry.setCondition(new BookEntryReadCondition(null, entry.getParents().get(0).getEntryId()));
                        } else if (entry.getParents().size() > 1) {
                            var conditions = entry.getParents().stream().map(parent ->
                                    new BookEntryReadCondition(null, parent.getEntryId())).toList();
                            var andCondition = new BookAndCondition(null, conditions.toArray(new BookEntryReadCondition[0]));
                            entry.setCondition(andCondition);
                        }
                    }
                }
            }
        }
    }

    /**
     * On server, called on datapack sync (because we need the data before we send the datapack sync packet) On client,
     * called on recipes updated, because recipes are available to the client only after datapack sync is complete
     */
    public boolean tryBuildBooks() {
        if (!this.booksBuilt) {
            Modonomicon.LOG.info("Building books ...");
            this.buildBooks();
            this.booksBuilt = true;
            Modonomicon.LOG.info("Books built.");

            Modonomicon.LOG.info("Adding read conditions ...");
            this.addReadConditions();
            Modonomicon.LOG.info("Read conditions added.");
            return true;
        }
        return false;
    }

    protected void onLoadingComplete() {
        this.loaded = true;
    }

    private Book loadBook(ResourceLocation key, JsonObject value) {
        return Book.fromJson(key, value);
    }

    private BookCategory loadCategory(ResourceLocation key, JsonObject value) {
        return BookCategory.fromJson(key, value);
    }

    private BookEntry loadEntry(ResourceLocation key, JsonObject value) {
        return BookEntry.fromJson(key, value);
    }

    private BookCommand loadCommand(ResourceLocation key, JsonObject value) {
        return BookCommand.fromJson(key, value);
    }

    private void categorizeContent(Map<ResourceLocation, JsonElement> content,
                                   HashMap<ResourceLocation, JsonObject> bookJsons,
                                   HashMap<ResourceLocation, JsonObject> categoryJsons,
                                   HashMap<ResourceLocation, JsonObject> entryJsons,
                                   HashMap<ResourceLocation, JsonObject> commandJsons
    ) {
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
                case "commands" -> {
                    commandJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                default -> {
                    Modonomicon.LOG.warn("Found unknown content for book '{}': '{}'. " +
                            "Should be one of: [File: book.json, Directory: entries/, Directory: categories/, Directory: commands/]", bookId, entry.getKey());
                    BookErrorManager.get().error(bookId, "Found unknown content for book '" + bookId + "': '" + entry.getKey() + "'. " +
                            "Should be one of: [File: book.json, Directory: entries/, Directory: categories/, Directory: commands/]");
                }
            }
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.preLoad();

        //TODO: handle datapack overrides, see TagLoader#load line 69 (refers to Tag.Builder#addFromJson)

        //first, load all json entries
        var bookJsons = new HashMap<ResourceLocation, JsonObject>();
        var categoryJsons = new HashMap<ResourceLocation, JsonObject>();
        var entryJsons = new HashMap<ResourceLocation, JsonObject>();
        var commandJsons = new HashMap<ResourceLocation, JsonObject>();
        this.categorizeContent(content, bookJsons, categoryJsons, entryJsons, commandJsons);

        BookErrorManager.get().setContext(""); //set to empty string to avoid using context helper internally
        //load books
        for (var entry : bookJsons.entrySet()) {
            try {
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);
                var book = this.loadBook(bookId, entry.getValue());
                this.books.put(book.getId(), book);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load book '" + entry.getKey() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }

        //load categories
        for (var entry : categoryJsons.entrySet()) {
            try {
                //load categories and link to book
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //category id skips the book id and the category directory
                var categoryId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
                var category = this.loadCategory(categoryId, entry.getValue());

                //link category and book
                var book = this.books.get(bookId);
                book.addCategory(category);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load category '" + entry.getKey() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }

        //load entries
        for (var entry : entryJsons.entrySet()) {
            try {
                //load entries and link to category
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //entry id skips the book id and the entries directory, but keeps category so it is unique
                var entryId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
                var bookEntry = this.loadEntry(entryId, entry.getValue());

                //link entry and category
                var book = this.books.get(bookId);
                var category = book.getCategory(bookEntry.getCategoryId());
                category.addEntry(bookEntry);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load entry '" + entry.getKey() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }

        //load commands
        for (var entry : commandJsons.entrySet()) {
            try {
                //load commands and link to book
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = new ResourceLocation(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //commands id skips the book id and the commands directory
                var commandId = new ResourceLocation(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));
                var command = this.loadCommand(commandId, entry.getValue());

                //link command and book
                var book = this.books.get(bookId);
                book.addCommand(command);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load command '" + entry.getKey() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }

        BookErrorManager.get().setContext(null); //set to null so we start using context helper internally
        this.onLoadingComplete();
    }
}
