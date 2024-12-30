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
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.CategoryLinkBookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.networking.SyncBookDataMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class BookDataManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = Data.MODONOMICON_DATA_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final BookDataManager instance = new BookDataManager();

    private final Map<ResourceLocation, Book> books = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private boolean loaded;
    private boolean booksBuilt;
    private HolderLookup.Provider registries;

    private BookDataManager() {
        super(GSON, FOLDER);
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

    public Map<ResourceLocation, Book> getBooks() {
        return this.books;
    }

    public Book getBook(ResourceLocation id) {
        return this.books.get(id);
    }

    public Message getSyncMessage() {
        //we hand over a copy of the map, because otherwise in SP scenarios if we clear this.books to prepare for receiving the message, we also clear the books in the message
        return new SyncBookDataMessage(this.books);
    }

    public boolean areBooksBuilt() {
        return this.booksBuilt;
    }

    public void onDatapackSyncPacket(SyncBookDataMessage message) {
        this.preLoad();
        this.books.putAll(message.books);
        this.onLoadingComplete();
    }

    public void onDatapackSync(ServerPlayer player) {

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
        this.booksBuilt = false;
        this.loaded = false;
        this.books.clear();
        BookErrorManager.get().reset();
    }

    public void buildBooks(Level level) {
        for (var book : this.books.values()) {
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
        Modonomicon.LOG.info("Pre-rendering markdown ...");
        for (var book : this.books.values()) {

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
            var macroLoaders = LoaderRegistry.getDynamicTextMacroLoaders(id);
            macroLoaders.forEach(loader -> loader.load().forEach(book::addMacro));
        });
    }

    protected void onLoadingComplete() {
        this.loaded = true;
    }

    private Book loadBook(ResourceLocation key, JsonObject value, HolderLookup.Provider provider) {
        return Book.fromJson(key, value, provider);
    }

    private BookCategory loadCategory(ResourceLocation key, JsonObject value, HolderLookup.Provider provider) {
        return BookCategory.fromJson(key, value, provider);
    }

    private BookEntry loadEntry(ResourceLocation id, JsonObject value, boolean autoAddReadConditions, HolderLookup.Provider provider) {
        if (value.has("type")) {
            ResourceLocation typeId = ResourceLocation.tryParse(value.get("type").getAsString());
            return LoaderRegistry.getEntryJsonLoader(typeId).fromJson(id, value, autoAddReadConditions, provider);
        }

        // This part here is for backwards compatibility and simplicity
        // If an entry does not have a type specified, ContentEntry is assumed
        // unless it has a property called "category_to_open" (CategoryLinkEntry)
        if (value.has("category_to_open")) {
            return CategoryLinkBookEntry.fromJson(id, value, autoAddReadConditions, provider);
        }
        return BookContentEntry.fromJson(id, value, autoAddReadConditions, provider);
    }

    private BookCommand loadCommand(ResourceLocation key, JsonObject value) {
        return BookCommand.fromJson(key, value);
    }

    /**
     * Loads only the condition on the given category, entry or page and runs testOnLoad.
     *
     * @param key        the resource location of the content
     * @param bookObject the json object representing the content
     * @return false if the condition is not met and the content should not be loaded.
     */
    private boolean testConditionOnLoad(ResourceLocation key, JsonObject bookObject, HolderLookup.Provider provider) {
        if (!bookObject.has("condition")) {
            return true; //no condition -> always load
        }

        return BookCondition.fromJson(key, bookObject.getAsJsonObject("condition"), provider).testOnLoad();
    }


    private void categorizeContent(Map<ResourceLocation, JsonElement> content,
                                   HashMap<ResourceLocation, JsonObject> bookJsons,
                                   HashMap<ResourceLocation, JsonObject> categoryJsons,
                                   HashMap<ResourceLocation, JsonObject> entryJsons,
                                   HashMap<ResourceLocation, JsonObject> commandJsons
    ) {
        for (var entry : content.entrySet()) {
            var pathParts = entry.getKey().getPath().split("/");

            var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
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

        //load books
        for (var entry : bookJsons.entrySet()) {
            try {
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);
                BookErrorManager.get().setContext("Loading Book JSON");
                var book = this.loadBook(bookId, entry.getValue(), this.registries);
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
                var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //category id skips the book id and the category directory
                var categoryId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

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
                var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //entry id skips the book id and the entries directory, but keeps category so it is unique
                var entryId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

                BookErrorManager.get().getContextHelper().entryId = entryId;
                //test if we should load the category at all
                if (!this.testConditionOnLoad(entryId, entry.getValue(), this.registries)) {
                    continue;
                }

                var bookEntry = this.loadEntry(entryId, entry.getValue(), this.books.get(bookId).autoAddReadConditions(), this.registries);

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

        //load commands
        for (var entry : commandJsons.entrySet()) {
            try {
                //load commands and link to book
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                BookErrorManager.get().setContext("Loading Command JSON");

                //commands id skips the book id and the commands directory
                var commandId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

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

    public static class Client extends SimpleJsonResourceReloadListener {

        private static final Client instance = new Client();

        private static final ResourceLocation fallbackFont = ResourceLocation.fromNamespaceAndPath("minecraft", "default");
        /**
         * Our local advancement cache, because we cannot just store random advancement in ClientAdvancements -> they get rejected
         */
        private final Map<ResourceLocation, AdvancementHolder> advancements = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        private final Object2FloatOpenHashMap<BookTextHolder.ScaleCacheKey> bookTextHolderScaleCache = new Object2FloatOpenHashMap<>();
        private boolean isFallbackLocale;
        private boolean isFontInitialized;

        public Client() {
            super(GSON, FOLDER);
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

        public ResourceLocation safeFont(ResourceLocation requested) {
            return this.useFallbackFont() ? fallbackFont : requested;
        }

        public AdvancementHolder getAdvancement(ResourceLocation id) {
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
        protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
            //reset on reload
            this.resetUseFallbackFont();
            this.advancements.clear();
            this.bookTextHolderScaleCache.clear();
        }
    }
}
