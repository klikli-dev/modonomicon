/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.CategoryLinkBookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.load.BookElementKind;
import com.klikli_dev.modonomicon.data.load.BookJsonPreprocessing;
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
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
     * Runs the load-time preprocessing (e.g. load conditions) on the given category, entry or page json.
     *
     * @return the json to parse, or empty if the element should be removed entirely.
     */
    private Optional<JsonObject> preprocess(BookElementKind kind, ResourceLocation bookId, ResourceLocation elementId, JsonObject json) {
        return BookJsonPreprocessing.process(kind, new BookLoadConditionContext(bookId, elementId, this.registries), json);
    }

    private record RemovedElement(ResourceLocation bookId, ResourceLocation elementId) {
    }

    private record PageFile(ResourceLocation fileKey, JsonObject json) {
    }

    /**
     * Groups file-based pages by the entry they belong to.
     * Page files live at {@code <book>/pages/<entry id path>/<file>.json}, e.g.
     * {@code data/modonomicon/modonomicon/books/demo/pages/features/test_entry/extra.json} targets entry
     * {@code modonomicon:features/test_entry}. The file name itself is informational only.
     */
    private Map<RemovedElement, List<PageFile>> groupPageFilesByEntry(Map<ResourceLocation, JsonObject> pageJsons) {
        var grouped = new HashMap<RemovedElement, List<PageFile>>();
        var sortedKeys = new ArrayList<>(pageJsons.keySet());
        sortedKeys.sort(Comparator.comparing(ResourceLocation::toString));
        for (var fileKey : sortedKeys) {
            var pathParts = fileKey.getPath().split("/");
            //book, "pages", at least one entry id part (category path + entry name), file name
            if (pathParts.length < 4 || !"pages".equals(pathParts[1])) {
                var bookId = pathParts.length > 0 ?
                        ResourceLocation.fromNamespaceAndPath(fileKey.getNamespace(), pathParts[0]) : null;
                BookErrorManager.get().setCurrentBookId(bookId);
                BookErrorManager.get().error("Failed to load page file '" + fileKey + "': expected path '<book>/pages/<category>/.../<entry>/<file>.json'. The file will be skipped.");
                BookErrorManager.get().reset();
                continue;
            }
            var bookId = ResourceLocation.fromNamespaceAndPath(fileKey.getNamespace(), pathParts[0]);
            var entryId = ResourceLocation.fromNamespaceAndPath(fileKey.getNamespace(),
                    String.join("/", Arrays.copyOfRange(pathParts, 2, pathParts.length - 1)));
            grouped.computeIfAbsent(new RemovedElement(bookId, entryId), k -> new ArrayList<>())
                    .add(new PageFile(fileKey, pageJsons.get(fileKey)));
        }
        return grouped;
    }

    /**
     * Whether the given page file json targets a specific inline page.
     * A file without any target adds a new page instead of overriding one.
     * Priority when several are present: id, then anchor, then index.
     */
    private static boolean hasPageTarget(JsonObject pageJson) {
        return isIdPageTarget(pageJson)
                || (pageJson.has("anchor") && !GsonHelper.getAsString(pageJson, "anchor", "").isEmpty())
                || pageJson.has("index");
    }

    private static boolean isIdPageTarget(JsonObject pageJson) {
        return pageJson.has("id") && !GsonHelper.getAsString(pageJson, "id", "").isEmpty();
    }

    /**
     * @return the position of the targeted inline page in the given working list, or -1 if there is no match.
     * An "id" identifies the page, so a missing id means "new page" for surviving files (handled by the caller).
     * "anchor" (only non-empty anchors match) and "index" (0-based, as authored) are pure selectors.
     */
    private static int findTargetedPageIndex(JsonArray pages, JsonObject pageJson) {
        if (isIdPageTarget(pageJson)) {
            var id = GsonHelper.getAsString(pageJson, "id", "");
            for (int i = 0; i < pages.size(); i++) {
                if (pages.get(i) instanceof JsonObject inline
                        && id.equals(GsonHelper.getAsString(inline, "id", ""))) {
                    return i;
                }
            }
            return -1;
        }
        if (pageJson.has("anchor")) {
            var anchor = GsonHelper.getAsString(pageJson, "anchor", "");
            if (!anchor.isEmpty()) {
                for (int i = 0; i < pages.size(); i++) {
                    if (pages.get(i) instanceof JsonObject inline
                            && anchor.equals(GsonHelper.getAsString(inline, "anchor", ""))) {
                        return i;
                    }
                }
                return -1;
            }
        }
        if (pageJson.has("index")) {
            var index = GsonHelper.getAsInt(pageJson, "index");
            if (index < 0 || index >= pages.size()) {
                return -1;
            }
            return index;
        }
        return -1;
    }

    /**
     * Merges file-based pages into an entry json, so single pages can be overridden, added or removed
     * without copying the entire entry.
     * <ul>
     * <li>A file with a matching "id" replaces that inline page, otherwise it is appended as a new page.</li>
     * <li>A file with a (non-empty) "anchor" or an "index" (0-based) replaces the first matching inline page,
     * errors if nothing matches.</li>
     * <li>A file without any target is appended; appended pages are ordered by "sort_number" (default: end).</li>
     * <li>A file removed by its (load_)condition removes the targeted inline page instead; without a target it is ignored.</li>
     * </ul>
     * Replacements keep the inline page's position; "index" and "sort_number" are merge-time only and are stripped.
     * Files are processed in file-path order; "index" targets resolve against the working list at that point.
     *
     * @return the (possibly copied) entry json to parse.
     */
    private JsonObject mergePageFiles(ResourceLocation bookId, ResourceLocation entryId, JsonObject entryJson, List<PageFile> pageFiles) {
        if (pageFiles == null || pageFiles.isEmpty()) {
            return entryJson;
        }

        record AppendedPage(JsonObject json, int sortNumber) {
        }

        var sorted = new ArrayList<>(pageFiles);
        sorted.sort(Comparator.comparing(file -> file.fileKey().toString()));

        var pages = new JsonArray();
        if (entryJson.has("pages")) {
            if (!(entryJson.get("pages") instanceof JsonArray inlinePages)) {
                //leave malformed pages untouched so the regular parsing reports the error
                BookErrorManager.get().setContext(null);
                return entryJson;
            }
            pages.addAll(inlinePages);
        }
        boolean changed = false;
        var appended = new ArrayList<AppendedPage>();

        for (var file : sorted) {
            BookErrorManager.get().getContextHelper().entryId = entryId;
            BookErrorManager.get().setContext("Page File: {}", file.fileKey());
            try {
                var processed = this.preprocess(BookElementKind.PAGE, bookId, entryId, file.json());
                if (processed.isEmpty()) {
                    //stripped by its (load_)condition: remove the targeted inline page, if any
                    if (hasPageTarget(file.json())) {
                        var targetIndex = findTargetedPageIndex(pages, file.json());
                        if (targetIndex < 0) {
                            BookErrorManager.get().error("Failed to merge page file '" + file.fileKey() + "': it targets a page in entry '" + entryId + "' that does not exist. The file will be skipped.");
                        } else {
                            pages.remove(targetIndex);
                            changed = true;
                        }
                    }
                    continue;
                }

                var pageJson = processed.get().deepCopy();
                pageJson.remove("index");
                var sortNumber = pageJson.has("sort_number") ? GsonHelper.getAsInt(pageJson, "sort_number", -1) : -1;
                pageJson.remove("sort_number");

                if (!hasPageTarget(pageJson)) {
                    appended.add(new AppendedPage(pageJson, sortNumber));
                    continue;
                }

                var targetIndex = findTargetedPageIndex(pages, pageJson);
                if (targetIndex < 0) {
                    if (isIdPageTarget(pageJson)) {
                        //an id doubles as the new page's identity: no match means a new page
                        appended.add(new AppendedPage(pageJson, sortNumber));
                    } else {
                        BookErrorManager.get().error("Failed to merge page file '" + file.fileKey() + "': it targets a page in entry '" + entryId + "' that does not exist. The file will be skipped.");
                    }
                    continue;
                }

                pages.set(targetIndex, pageJson);
                changed = true;
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to merge page file '" + file.fileKey() + "'", e);
            }
        }

        if (!appended.isEmpty()) {
            //order appended pages by sort_number, files without one go last (stable for equal values)
            appended.sort(Comparator.comparingInt(page -> page.sortNumber() < 0 ? Integer.MAX_VALUE : page.sortNumber()));
            for (var page : appended) {
                pages.add(page.json());
            }
            changed = true;
        }

        BookErrorManager.get().setContext(null);

        if (!changed) {
            return entryJson;
        }

        var result = entryJson.deepCopy();
        result.add("pages", pages);
        return result;
    }


    private void categorizeContent(Map<ResourceLocation, JsonElement> content,
                                   HashMap<ResourceLocation, JsonObject> bookJsons,
                                   HashMap<ResourceLocation, JsonObject> categoryJsons,
                                   HashMap<ResourceLocation, JsonObject> entryJsons,
                                   HashMap<ResourceLocation, JsonObject> pageJsons,
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
                case "pages" -> {
                    pageJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                case "commands" -> {
                    commandJsons.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
                default -> {
                    Modonomicon.LOG.warn("Found unknown content for book '{}': '{}'. " +
                            "Should be one of: [File: book.json, Directory: entries/, Directory: categories/, Directory: pages/, Directory: commands/]", bookId, entry.getKey());
                    BookErrorManager.get().error(bookId, "Found unknown content for book '" + bookId + "': '" + entry.getKey() + "'. " +
                            "Should be one of: [File: book.json, Directory: entries/, Directory: categories/, Directory: pages/, Directory: commands/]");
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
        var pageJsons = new HashMap<ResourceLocation, JsonObject>();
        var commandJsons = new HashMap<ResourceLocation, JsonObject>();
        this.categorizeContent(content, bookJsons, categoryJsons, entryJsons, pageJsons, commandJsons);

        //group file-based pages by their entry, they are merged into the entry jsons below
        var pageFilesByEntry = this.groupPageFilesByEntry(pageJsons);

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
        var removedCategories = new HashSet<RemovedElement>();
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
                //load conditions (and, for backwards compatibility, legacy conditions) can remove the category entirely
                var categoryJson = this.preprocess(BookElementKind.CATEGORY, bookId, categoryId, entry.getValue());
                if (categoryJson.isEmpty()) {
                    removedCategories.add(new RemovedElement(bookId, categoryId));
                    BookErrorManager.get().reset();
                    continue;
                }

                var category = this.loadCategory(categoryId, categoryJson.get(), this.registries);

                //link category and book
                var book = this.books.get(bookId);
                if (book == null) {
                    BookErrorManager.get().error("Failed to load category '" + entry.getKey() + "': book '" + bookId + "' was not loaded (missing or invalid book.json). The category will be skipped.");
                    BookErrorManager.get().reset();
                    continue;
                }
                book.addCategory(category);

                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load category '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //load entries
        var removedEntries = new HashSet<RemovedElement>();
        for (var entry : entryJsons.entrySet()) {
            try {
                //load entries and link to category
                var pathParts = entry.getKey().getPath().split("/");
                var bookId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), pathParts[0]);
                BookErrorManager.get().setCurrentBookId(bookId);

                //entry id skips the book id and the entries directory, but keeps category so it is unique
                var entryId = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), Arrays.stream(pathParts).skip(2).collect(Collectors.joining("/")));

                BookErrorManager.get().getContextHelper().entryId = entryId;

                //claim the file-based pages for this entry (even if the entry itself is removed below,
                //so they are not reported as orphaned)
                var pageFiles = pageFilesByEntry.remove(new RemovedElement(bookId, entryId));

                //entries of removed categories are removed as well
                var rawCategoryId = entry.getValue().has("category") ? entry.getValue().get("category").getAsString() : null;
                if (rawCategoryId != null) {
                    var rawCategoryResourceId = rawCategoryId.contains(":") ?
                            ResourceLocation.parse(rawCategoryId) :
                            ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), rawCategoryId);
                    if (removedCategories.contains(new RemovedElement(bookId, rawCategoryResourceId))) {
                        removedEntries.add(new RemovedElement(bookId, entryId));
                        BookErrorManager.get().reset();
                        continue;
                    }
                }

                //load conditions (and, for backwards compatibility, legacy conditions) can remove the entry entirely.
                //for content entries this also filters the inline pages.
                //file-based pages are merged into the entry json first, so they take part in all of the above.
                var mergedEntryJson = this.mergePageFiles(bookId, entryId, entry.getValue(), pageFiles);
                if (pageFiles != null && !pageFiles.isEmpty()) {
                    //merging sets page file error context, restore the entry context
                    BookErrorManager.get().reset();
                    BookErrorManager.get().setCurrentBookId(bookId);
                    BookErrorManager.get().getContextHelper().entryId = entryId;
                }

                var processedEntryJson = this.preprocess(BookElementKind.ENTRY, bookId, entryId, mergedEntryJson);
                if (processedEntryJson.isEmpty()) {
                    removedEntries.add(new RemovedElement(bookId, entryId));
                    BookErrorManager.get().reset();
                    continue;
                }

                var book = this.books.get(bookId);
                if (book == null) {
                    BookErrorManager.get().error("Failed to load entry '" + entry.getKey() + "': book '" + bookId + "' was not loaded (missing or invalid book.json). The entry will be skipped.");
                    BookErrorManager.get().reset();
                    continue;
                }

                var bookEntry = this.loadEntry(entryId, processedEntryJson.get(), book.autoAddReadConditions(), this.registries);

                //link entry and category
                var category = book.getCategory(bookEntry.getCategoryId());
                if (category == null) {
                    BookErrorManager.get().error("Failed to load entry '" + entry.getKey() + "': category '" + bookEntry.getCategoryId() + "' not found in book '" + bookId + "'. The entry will be skipped.");
                    BookErrorManager.get().reset();
                    continue;
                }
                category.addEntry(bookEntry);

                BookErrorManager.get().reset();
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to load entry '" + entry.getKey() + "'", e);
                BookErrorManager.get().reset();
            }
        }

        //report file-based pages that target entries which were never loaded (missing entry or book)
        var orphanedPageFiles = new ArrayList<>(pageFilesByEntry.entrySet());
        orphanedPageFiles.sort(Comparator.comparing(group -> group.getKey().toString()));
        for (var group : orphanedPageFiles) {
            var bookId = group.getKey().bookId();
            var entryId = group.getKey().elementId();
            BookErrorManager.get().setCurrentBookId(bookId);
            BookErrorManager.get().getContextHelper().entryId = entryId;
            for (var file : group.getValue()) {
                BookErrorManager.get().setContext("Page File: {}", file.fileKey());
                BookErrorManager.get().error("Failed to merge page file '" + file.fileKey() + "': entry '" + entryId + "' was not loaded (missing or invalid entry). The file will be skipped.");
            }
            BookErrorManager.get().reset();
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
                if (book == null) {
                    BookErrorManager.get().error("Failed to load command '" + entry.getKey() + "': book '" + bookId + "' was not loaded (missing or invalid book.json). The command will be skipped.");
                    BookErrorManager.get().reset();
                    continue;
                }
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
