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
import com.mojang.serialization.Codec;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
 import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.runtime.RuntimeBookContentManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.networking.SyncBookDataMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.registry.DynamicTextMacroRegistry;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class BookDataManager extends SimpleJsonResourceReloadListener<JsonElement> {
    public static final String FOLDER = Data.MODONOMICON_DATA_PATH;

    private static final BookDataManager instance = new BookDataManager();

    private final Map<Identifier, Book> books = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private boolean loaded;
    private boolean booksBuilt;
    private HolderLookup.Provider registries;
    private MinecraftServer server;
    private boolean loadingFromSyncPacket;
    private ResourceKey<Level> buildDimension = Level.OVERWORLD;

    private BookDataManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json(FOLDER));
    }

    public static BookDataManager get() {
        return instance;
    }

    public void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public Map<Identifier, Book> getBooks() {
        return this.books;
    }

    public Book getBook(Identifier id) {
        return this.books.get(id);
    }

    public Message getSyncMessage() {
        //the message constructor will make a copy of the map, because otherwise in SP scenarios if we clear this.books to prepare for receiving the message, we also clear the books in the message
        return new SyncBookDataMessage(this.books);
    }

    public Message getSyncMessage(Set<Identifier> bookIds) {
        var books = new LinkedHashMap<Identifier, Book>();
        for (var bookId : bookIds) {
            var book = this.books.get(bookId);
            if (book != null) {
                books.put(bookId, book);
            }
        }
        return new SyncBookDataMessage(books, false);
    }

    public boolean areBooksBuilt() {
        return this.booksBuilt;
    }

    public void onDatapackSyncPacket(SyncBookDataMessage message) {
        this.loadingFromSyncPacket = true;
        try {
            if (message.replaceAll) {
                this.preLoad();
            }
            this.books.putAll(message.books);
            this.onLoadingComplete();
        } finally {
            this.loadingFromSyncPacket = false;
        }
    }

    public void onDatapackSync(ServerPlayer player) {
        this.server = player.level().getServer();

        this.tryBuildBooks(player.level()); //lazily build books when first client connects

        //If integrated server and host (= SP or lan host), don't send as we already have it
        if(player.connection.connection.isMemoryConnection())
            return;

        Message syncMessage = this.getSyncMessage();

        Services.NETWORK.sendToSplit(player, syncMessage);
    }

    public void onRecipesUpdated(Level level) {
        Client.get().resetUseFallbackFont();
        this.tryBuildBooks(level);
        this.prerenderMarkdown(level.registryAccess());
    }

    public void preLoad() {
        RuntimeBookContentManager.get().onBooksPreLoad();
        this.booksBuilt = false;
        this.loaded = false;
        this.books.clear();
        BookErrorManager.get().reset();
    }

    public void buildBooks(Level level) {
        this.buildBooks(this.books.values(), level);
    }

    public void buildBooks(Collection<Book> books, Level level) {
        for (var book : books) {
            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());
            try {
                book.build(level);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to build book '" + book.getId() + "'", e);
            }
            BookErrorManager.get().setCurrentBookId(null);
        }
    }

    public void prerenderMarkdown(HolderLookup.Provider provider) {
        this.prerenderMarkdown(this.books.values(), provider);
    }

    public void prerenderMarkdown(Collection<Book> books, HolderLookup.Provider provider) {
        Modonomicon.LOG.info("Pre-rendering markdown ...");
        for (var book : books) {

            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());

            //TODO: allow modders to configure this renderer
            var textRenderer = new BookTextRenderer(book, provider);

            if (!BookErrorManager.get().hasErrors(book.getId())) {
                try {
                    book.prerenderMarkdown(textRenderer);
                } catch (Exception e) {
                    BookErrorManager.get().error("Failed to render markdown for book '" + book.getId() + "'", e);
                }
            } else {
                BookErrorManager.get().error("Cannot render markdown for book '" + book.getId() + " because of errors during book build'");
            }

            BookErrorManager.get().setCurrentBookId(null);
        }
        Modonomicon.LOG.info("Finished pre-rendering markdown.");
    }

    /**
     * On server, called on datapack sync (because we need the data before we send the datapack sync packet) On client,
     * called on recipes updated, because recipes are available to the client only after datapack sync is complete
     */
    public boolean tryBuildBooks(Level level) {
        if (this.booksBuilt) {
            return false;
        }

        if(!level.isClientSide()){
            this.server = level.getServer();
            this.buildDimension = level.dimension();
            this.resolveMacros(); //macros are only resolved serverside, the resolved macros are then stored in the book.
        }

        Modonomicon.LOG.info("Building books ...");
        this.buildBooks(level);
        this.booksBuilt = true;
        Modonomicon.LOG.info("Books built.");
        return true;
    }

    public void resolveMacros(){
        this.getBooks().forEach((id, book) -> {
            var macroLoaders = DynamicTextMacroRegistry.getLoaders(id);
            macroLoaders.forEach(loader -> loader.load().forEach(book::addMacro));
        });
    }

    protected void onLoadingComplete() {
        this.loaded = true;
        if (!this.loadingFromSyncPacket) {
            RuntimeBookContentManager.get().onBooksLoaded();
        }
    }

    public void rebuildBooks(Set<Identifier> bookIds) {
        if (this.server == null || bookIds.isEmpty()) {
            return;
        }

        var books = this.getBooks(bookIds);
        if (books.isEmpty()) {
            return;
        }

        var level = this.server.getLevel(this.buildDimension);
        if (level == null) {
            level = this.server.overworld();
        }

        this.buildBooks(books, level);
        this.prerenderMarkdown(books, this.server.registryAccess());
    }

    public void syncBooks(Set<Identifier> bookIds) {
        if (this.server == null || bookIds.isEmpty()) {
            return;
        }

        Message syncMessage = this.getSyncMessage(bookIds);
        for (var player : this.server.getPlayerList().getPlayers()) {
            if (player.connection.connection.isMemoryConnection()) {
                continue;
            }

            Services.NETWORK.sendToSplit(player, syncMessage);
        }
    }

    private Set<Book> getBooks(Set<Identifier> bookIds) {
        var books = new LinkedHashSet<Book>();
        for (var bookId : bookIds) {
            var book = this.books.get(bookId);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    public RegistryAccess registryAccess() {
        if (this.registries instanceof RegistryAccess registryAccess) {
            return registryAccess;
        }

        return RegistryAccess.EMPTY;
    }

    private Book loadBook(Identifier key, JsonObject value, BookThemeData themeData, HolderLookup.Provider provider) {
        return Book.fromJson(key, value, themeData, provider);
    }

    private BookThemeData loadTheme(JsonObject value) {
        return BookThemeData.fromJson(value);
    }

    private BookCategory loadCategory(Identifier key, JsonObject value, HolderLookup.Provider provider) {
        return BookCategory.fromJson(key, value, provider);
    }

    private BookEntry loadEntry(JsonObject value, HolderLookup.Provider provider) {
        return BookEntry.fromJson(value, provider);
    }

    private BookCommand loadCommand(Identifier key, JsonObject value) {
        return BookCommand.fromJson(key, value);
    }

    /**
     * Loads only the condition on the given category, entry or page and runs testOnLoad.
     *
     * @param key        the resource location of the content
     * @param bookObject the json object representing the content
     * @return false if the condition is not met and the content should not be loaded.
     */
    private boolean testConditionOnLoad(Identifier key, JsonObject bookObject, HolderLookup.Provider provider) {
        if (!bookObject.has("condition")) {
            return true; //no condition -> always load
        }

        return BookCondition.fromJson(key, bookObject.getAsJsonObject("condition"), provider).testOnLoad();
    }


   private void categorizeContent(Map<Identifier, JsonElement> content,
                                    HashMap<Identifier, JsonObject> bookJsons,
                                    HashMap<Identifier, JsonObject> themeJsons,
                                    HashMap<Identifier, JsonObject> categoryJsons,
                                    HashMap<Identifier, JsonObject> entryJsons,
                                    HashMap<Identifier, JsonObject> commandJsons,
                                    HashMap<Identifier, JsonObject> pageJsons
    ) {
        for (var entry : content.entrySet()) {
            var pathParts = entry.getKey().getPath().split("/");

            var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
            switch (pathParts[1]) {
                case "book" -> {
                    bookJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "theme" -> {
                    themeJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "entries" -> {
                    // Check if this is a page file (path contains "pages" directory)
                    boolean isPageFile = false;
                    for (int i = 2; i < pathParts.length - 1; i++) {
                        if ("pages".equals(pathParts[i])) {
                            isPageFile = true;
                            break;
                        }
                    }
                    if (isPageFile) {
                        pageJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                    } else {
                        entryJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                    }
                }
                case "categories" -> {
                    categoryJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "commands" -> {
                    commandJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                default -> {
                    Modonomicon.LOG.warn("Found unknown content for book '{}': '{}'. " +
                            "Should be one of: [File: book.json, File: theme.json, Directory: entries/, Directory: entries/<category>/<entry>/pages/, Directory: categories/, Directory: commands/]", bookId, entry.getKey());
                    BookErrorManager.get().error(bookId, "Found unknown content for book '" + bookId + "': '" + entry.getKey() + "'. " +
                            "Should be one of: [File: book.json, File: theme.json, Directory: entries/, Directory: entries/<category>/<entry>/pages/, Directory: categories/, Directory: commands/]");
                }
            }
        }
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> content, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.preLoad();

        //TODO: handle datapack overrides, see TagLoader#load line 69 (refers to Tag.Builder#addFromJson)

        //first, load all json entries
        var bookJsons = new HashMap<Identifier, JsonObject>();
        var themeJsons = new HashMap<Identifier, JsonObject>();
        var categoryJsons = new HashMap<Identifier, JsonObject>();
        var entryJsons = new HashMap<Identifier, JsonObject>();
        var commandJsons = new HashMap<Identifier, JsonObject>();
        var pageJsons = new HashMap<Identifier, JsonObject>();
        this.categorizeContent(content, bookJsons, themeJsons, categoryJsons, entryJsons, commandJsons, pageJsons);

        var themeDataByBook = new HashMap<Identifier, BookThemeData>();
        for (var entry : themeJsons.entrySet()) {
            try {
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);
                BookErrorManager.get().setContext("Loading Theme JSON");
                themeDataByBook.put(bookId, this.loadTheme(entry.getValue()));
                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load theme '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //load books
        for (var entry : bookJsons.entrySet()) {
            try {
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);
                BookErrorManager.get().setContext("Loading Book JSON");
                var themeData = themeDataByBook.getOrDefault(bookId, BookThemeData.defaults());
                var book = this.loadBook(bookId, entry.getValue(), themeData, this.registries);
                this.books.put(book.getId(), book);
                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load book '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //load categories
        for (var entry : categoryJsons.entrySet()) {
            try {
                //load categories and link to book
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //category id skips the book id and the category directory
                var categoryId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

                BookErrorManager.get().getContextHelper().categoryId = categoryId;
                //test if we should load the category at all
                if (!this.testConditionOnLoad(categoryId, entry.getValue(), this.registries)) {
                    continue;
                }

                var category = this.loadCategory(categoryId, entry.getValue(), this.registries);

                //link category and book
                var book = this.books.get(bookId);
                book.addCategory(category);

                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load category '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //load entries
        for (var entry : entryJsons.entrySet()) {
            try {
                //load entries and link to category
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //entry id skips the book id and the entries directory, but keeps category so it is unique
                var sourceEntryId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

                BookErrorManager.get().getContextHelper().entryId = sourceEntryId;
                //test if we should load the category at all
                if (!this.testConditionOnLoad(sourceEntryId, entry.getValue(), this.registries)) {
                    continue;
                }

                var bookEntry = this.loadEntry(entry.getValue(), this.registries);
                BookErrorManager.get().getContextHelper().entryId = bookEntry.getId();

                //link entry and category
                var book = this.books.get(bookId);
                var category = book.getCategory(bookEntry.getCategoryId());
                category.addEntry(bookEntry);

                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load entry '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //Merge per-page JSON files into BookContentEntry inline page lists
        this.mergePageJsons(pageJsons);

        //load commands
        for (var entry : commandJsons.entrySet()) {
            try {
                //load commands and link to book
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                BookErrorManager.get().setContext("Loading Command JSON");

                //commands id skips the book id and the commands directory
                var commandId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

                BookErrorManager.get().setContext("Loading Command JSON: " + commandId);
                var command = this.loadCommand(commandId, entry.getValue());

                //link command and book
                var book = this.books.get(bookId);
                book.addCommand(command);
                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load command '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        BookErrorManager.get().reset();

        this.onLoadingComplete();
    }

    /**
     * Merges per-page JSON files into BookContentEntry inline page lists.
     * Pages in a file replace inline pages with the same ID.
     * Pages without a matching inline ID are inserted at sort_number position (default: end of list).
     */
    private void mergePageJsons(Map<Identifier, JsonObject> pageJsons) {
        var sortedEntries = new ArrayList<>(pageJsons.entrySet());
        sortedEntries.sort(Comparator.comparing(e -> e.getKey().toString()));
        for (var entry : sortedEntries) {
            try {
                var path = entry.getKey();
                var pathParts = path.getPath().split("/");

                // Path: modonomicon/<book>/entries/<category...>/<entry-id>/pages/<page-id>.json
                // Find the "pages" segment to correctly handle nested categories and entry IDs
                int pagesIdx = -1;
                for (int i = 0; i < pathParts.length; i++) {
                    if ("pages".equals(pathParts[i])) {
                        pagesIdx = i;
                        break;
                    }
                }

                if (pagesIdx < 4) {
                    BookErrorManager.get().error("Invalid page file path structure: " + path);
                    continue;
                }

                var bookId = Identifier.fromNamespaceAndPath(path.getNamespace(), pathParts[0]);

                // Category: all segments between "entries" and "pages" minus the entry name
                var categoryPath = Arrays.stream(pathParts)
                        .skip(2)
                        .limit(pagesIdx - 3)
                        .collect(Collectors.joining("/"));
                var categoryId = Identifier.fromNamespaceAndPath(path.getNamespace(), categoryPath);

                // Entry ID: category path + "/" + entry name (part just before "pages")
                var entryPath = categoryPath + "/" + pathParts[pagesIdx - 1];
                var entryId = Identifier.fromNamespaceAndPath(path.getNamespace(), entryPath);

                BookErrorManager.get().setCurrentBookId(bookId);
                BookErrorManager.get().getContextHelper().entryId = entryId;

                var book = this.books.get(bookId);
                if (book == null) {
                    BookErrorManager.get().error("Page file references unknown book: " + bookId);
                    continue;
                }

                var category = book.getCategory(categoryId);
                if (category == null) {
                    BookErrorManager.get().error("Page file references unknown category: " + categoryId);
                    continue;
                }

                var bookEntry = category.getEntry(entryId);
                if (bookEntry == null) {
                    BookErrorManager.get().error("Page file references unknown entry: " + entryId);
                    continue;
                }

                if (!(bookEntry instanceof BookContentEntry contentEntry)) {
                    BookErrorManager.get().error("Page file references non-content entry: " + entryId);
                    continue;
                }

                // Parse the page JSON
                var page = BookPage.fromJson(entryId, entry.getValue(), this.registries);

                // Read sort_number if present (default: -1 = append at end)
                var sortNumber = GsonHelper.getAsInt(entry.getValue(), "sort_number", -1);

                // Merge: replace inline page with same ID, or insert at sort_number
                this.mergePageIntoEntry(contentEntry, page, sortNumber);

                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load page file '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }
    }

    /**
     * Merges a single page into a BookContentEntry's page list.
     * If an inline page has the same ID, it is replaced.
     * Otherwise, the page is inserted at the given sortNumber position (default -1 = append at end).
     * Uses the page's own ID (page.getId()) as the authoritative identifier for the merge check.
     */
    private void mergePageIntoEntry(BookContentEntry contentEntry, BookPage page, int sortNumber) {
        var pages = contentEntry.getPages();
        // Try to find and replace an inline page with the same ID
        for (int i = 0; i < pages.size(); i++) {
            var existing = pages.get(i);
            var existingId = existing.getId();
            if (existingId == null) {
                continue;
            }
            if (existingId.equals(page.getId())) {
                pages.set(i, page);
                return;
            }
        }

        // No matching ID — insert at sortNumber position or append
        if (sortNumber < 0) {
            pages.add(page);
        } else {
            var insertIndex = Math.min(sortNumber, pages.size());
            pages.add(insertIndex, page);
        }
    }

    public static class Client extends SimpleJsonResourceReloadListener<JsonElement> {

        private static final Client instance = new Client();

        private static final Identifier fallbackFont = Identifier.fromNamespaceAndPath("minecraft", "default");
        /**
         * Our local advancement cache, because we cannot just store random advancement in ClientAdvancements -> they get rejected
         */
        private final Map<Identifier, AdvancementHolder> advancements = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        private final Object2FloatOpenHashMap<BookTextHolder.ScaleCacheKey> bookTextHolderScaleCache = new Object2FloatOpenHashMap<>();
        private boolean isFallbackLocale;
        private boolean isFontInitialized;

        public Client() {
            super(ExtraCodecs.JSON, FileToIdConverter.json(FOLDER));
            this.bookTextHolderScaleCache.defaultReturnValue(-1f);
        }

        public static Client get() {
            return instance;
        }

        public void resetUseFallbackFont() {
            this.isFontInitialized = false;
        }

        public boolean useFallbackFont() {
            if (!this.isFontInitialized) {
                this.isFontInitialized = true;

                var locale = Minecraft.getInstance().getLanguageManager().getSelected();
                this.isFallbackLocale = ClientServices.CLIENT_CONFIG.fontFallbackLocales().stream().anyMatch(l -> l.equals(locale));
            }

            return this.isFallbackLocale;
        }

        public Identifier safeFont(Identifier requested) {
            return this.useFallbackFont() ? fallbackFont : requested;
        }

        public AdvancementHolder getAdvancement(Identifier id) {
            return this.advancements.get(id);
        }

        public void putScale(BookTextHolder holder, int width, int height, float scale) {
            this.bookTextHolderScaleCache.put(new BookTextHolder.ScaleCacheKey(holder, width, height), scale);
        }

        /**
         * Returns -1 if no scale is found
         */
        public float getScale(BookTextHolder holder, int width, int height) {
            return this.bookTextHolderScaleCache.getFloat(new BookTextHolder.ScaleCacheKey(holder, width, height));
        }

        public void addAdvancement(AdvancementHolder advancement) {
            this.advancements.put(advancement.id(), advancement);
        }

        @Override
        protected void apply(Map<Identifier, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
            //reset on reload
            this.resetUseFallbackFont();
            this.advancements.clear();
            this.bookTextHolderScaleCache.clear();
        }
    }
}
