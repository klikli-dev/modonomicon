/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookTheme;
import com.klikli_dev.modonomicon.registry.ThemeRegistry;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.klikli_dev.modonomicon.util.Codecs;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Book {
    protected Identifier id;
    protected String name;
    protected BookTextHolder description;
    protected String tooltip;
    protected String creativeTab;


    protected Identifier model;
    protected Identifier turnPageSound;
    protected Map<Identifier, BookCategory> categories;
    protected Map<Identifier, BookEntry> entries;
    protected Map<Identifier, BookCommand> commands;


    protected boolean generateBookItem;
    @Nullable
    protected Identifier customBookItem;

    protected Identifier font;
    protected BookThemeData themeData;
    protected transient BookTheme theme;

    /**
     * The display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style)
     * If the book is in index mode then all categories will also be shown in index mode. If the book is in node mode, then individual categories can be in index mode.
     * If in index mode, the themed double-page background is used instead of the node frame texture.
     */
    protected BookDisplayMode displayMode;

    protected Identifier leafletEntry;

    protected PageDisplayMode pageDisplayMode = PageDisplayMode.DOUBLE_PAGE;

    /**
     * If true, invalid links do not show an error screen when opening the book.
     * Instead, the book and pages will open, but the link will not work.
     */
    protected boolean allowOpenBooksWithInvalidLinks;

    /**
     * If true, a "Recently Unlocked" button is shown that lists entries sorted by unlock timestamp.
     * Useful for books with many conditional entries. Defaults to true.
     */
    protected boolean showRecentlyUnlocked;

    /**
     * A map of macros. This is filled automatically based on DynamicTextMacroRegistry registrations, not loaded from JSON.
     */
    protected final Map<String, String> textMacros = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public Book(Identifier id, String name, BookTextHolder description, String tooltip, Identifier model, BookDisplayMode displayMode, boolean generateBookItem,
                @Nullable Identifier customBookItem, String creativeTab, Identifier font, Identifier turnPageSound, Identifier leafletEntry,
                PageDisplayMode pageDisplayMode, boolean allowOpenBooksWithInvalidLinks, boolean showRecentlyUnlocked,
                BookThemeData themeData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tooltip = tooltip;
        this.model = model;
        this.displayMode = displayMode;
        this.generateBookItem = generateBookItem;
        this.customBookItem = customBookItem;
        this.creativeTab = creativeTab;
        this.font = font;
        this.turnPageSound = turnPageSound;
        this.categories = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.entries = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.commands = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

        this.leafletEntry = leafletEntry;

        this.pageDisplayMode = pageDisplayMode;

        this.allowOpenBooksWithInvalidLinks = allowOpenBooksWithInvalidLinks;

        this.showRecentlyUnlocked = showRecentlyUnlocked;
        this.themeData = themeData;
    }

    public static Book fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return fromJson(id, json, BookThemeData.defaults(), provider);
    }

    public static Book fromJson(Identifier id, JsonObject json, BookThemeData themeData, HolderLookup.Provider provider) {
        var name = GsonHelper.getAsString(json, "name");
        var description = BookGsonHelper.getAsBookTextHolder(json, "description", BookTextHolder.EMPTY, provider);
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = Identifier.parse(GsonHelper.getAsString(json, "model", ModonomiconConstants.Data.Book.DEFAULT_MODEL));
        var generateBookItem = GsonHelper.getAsBoolean(json, "generate_book_item", true);
        var displayMode = BookDisplayMode.byName(GsonHelper.getAsString(json, "display_mode", BookDisplayMode.NODE.getSerializedName()));
        var customBookItem = json.has("custom_book_item") ?
                Identifier.parse(GsonHelper.getAsString(json, "custom_book_item")) :
                null;
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");

        var font = Identifier.parse(GsonHelper.getAsString(json, "font", ModonomiconConstants.Data.Book.DEFAULT_FONT));

        var turnPageSound = Identifier.parse(GsonHelper.getAsString(json, "turn_page_sound", ModonomiconConstants.Data.Book.DEFAULT_PAGE_TURN_SOUND));
        Identifier leafletEntry = null;
        if (json.has("leaflet_entry")) {
            leafletEntry = Codecs.parseStrictIdentifier(GsonHelper.getAsString(json, "leaflet_entry"));
        }

        var pageDisplayMode = PageDisplayMode.byName(GsonHelper.getAsString(json, "page_display_mode", PageDisplayMode.DOUBLE_PAGE.getSerializedName()));

        var allowOpenBooksWithInvalidLinks = GsonHelper.getAsBoolean(json, "allow_open_book_with_invalid_links", false);

        var showRecentlyUnlocked = GsonHelper.getAsBoolean(json, "show_recently_unlocked", true);

        return new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font,
                turnPageSound, leafletEntry, pageDisplayMode, allowOpenBooksWithInvalidLinks, showRecentlyUnlocked, themeData);
    }


    public static Book fromNetwork(Identifier id, RegistryFriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var description = BookTextHolder.fromNetwork(buffer);
        var tooltip = buffer.readUtf();
        var model = buffer.readIdentifier();
        var displayMode = BookDisplayMode.byId(buffer.readByte());
        var themeData = BookThemeData.fromNetwork(buffer);

        var generateBookItem = buffer.readBoolean();
        var customBookItem = buffer.readNullable(FriendlyByteBuf::readIdentifier);
        var creativeTab = buffer.readUtf();

        var font = buffer.readIdentifier();

        var turnPageSound = buffer.readIdentifier();
        var leafletEntry = buffer.readNullable(FriendlyByteBuf::readIdentifier);

        var pageDisplayMode = PageDisplayMode.byId(buffer.readByte());

        var allowOpenBooksWithInvalidLinks = buffer.readBoolean();

        var showRecentlyUnlocked = buffer.readBoolean();

        var textMacros = buffer.readMap((b) -> b.readUtf(), (b) -> b.readUtf()); //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#readMap
        var book = new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font,
                turnPageSound, leafletEntry, pageDisplayMode, allowOpenBooksWithInvalidLinks, showRecentlyUnlocked, themeData);

        book.textMacros().putAll(textMacros);

        return book;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Level level) {
        //first "backlink" all our entries directly into the book
        for (var category : this.categories.values()) {
            for (var entry : category.getEntries().values()) {
                this.addEntry(entry);
            }
        }

        //then build categories, which will in turn build entries (which need the above backlinks to resolve parents)
        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.build(level, this);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }

        for (var command : this.commands.values()) {
            command.build(this);
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        if (!this.description.hasComponent()) {
            this.description = new RenderedBookTextHolder(this.description, textRenderer.render(this.description.getString()));
        }

        for (var category : this.categories.values()) {
            BookErrorManager.get().getContextHelper().categoryId = category.getId();
            category.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().categoryId = null;
        }
    }

    public void addMacro(String key, String value) {
        this.textMacros.put(key, value);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        this.description.toNetwork(buffer);
        buffer.writeUtf(this.tooltip);
        buffer.writeIdentifier(this.model);
        buffer.writeByte(this.displayMode.ordinal());
        this.themeData.toNetwork(buffer);

        buffer.writeBoolean(this.generateBookItem);

        buffer.writeNullable(this.customBookItem, FriendlyByteBuf::writeIdentifier);

        buffer.writeUtf(this.creativeTab);

        buffer.writeIdentifier(this.font);

        buffer.writeIdentifier(this.turnPageSound);
        buffer.writeNullable(this.leafletEntry, FriendlyByteBuf::writeIdentifier);

        buffer.writeByte(this.pageDisplayMode.ordinal());

        buffer.writeBoolean(this.allowOpenBooksWithInvalidLinks);
        buffer.writeBoolean(this.showRecentlyUnlocked);
        buffer.writeMap(this.textMacros, (b, v) -> b.writeUtf(v), (b, v) -> b.writeUtf(v));  //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#writeMap
    }

    public Identifier getTurnPageSound() {
        return this.turnPageSound;
    }

    public Identifier getId() {
        return this.id;
    }

    public Map<String, String> textMacros() {
        return this.textMacros;
    }

    public void addCategory(BookCategory category) {
        this.categories.putIfAbsent(category.id, category);
    }

    public BookCategory getCategory(Identifier id) {
        return this.categories.get(id);
    }

    public Map<Identifier, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted() {
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.getId(), entry);
    }

    public BookEntry getEntry(Identifier id) {
        return this.entries.get(id);
    }

    public Map<Identifier, BookEntry> getEntries() {
        return this.entries;
    }

    public void addCommand(BookCommand command) {
        this.commands.putIfAbsent(command.id, command);
    }

    public Map<Identifier, BookCommand> getCommands() {
        return this.commands;
    }

    public BookCommand getCommand(Identifier id) {
        return this.commands.get(id);
    }

    public String getName() {
        return this.name;
    }

    public BookTextHolder getDescription() {
        return this.description;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getCreativeTab() {
        return this.creativeTab;
    }

    public Identifier getFont() {
        return this.font;
    }

    @Nullable
    public Identifier getCustomBookItem() {
        return this.customBookItem;
    }

    public Identifier getModel() {
        return this.model;
    }

    public BookDisplayMode getDisplayMode() {
        if (this.isLeaflet()) {
            return BookDisplayMode.INDEX;
        }
        return this.displayMode;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    public Identifier getLeafletEntry() {
        return this.leafletEntry;
    }

    public boolean isLeaflet() {
        return this.leafletEntry != null;
    }

    public BookAddress getLeafletAddress() {
        var leafletEntry = this.getEntry(this.leafletEntry);
        return BookAddress.ignoreSaved(leafletEntry);
    }

    public PageDisplayMode getPageDisplayMode() {
        return this.pageDisplayMode;
    }

    public BookThemeData themeData() {
        return this.themeData;
    }

    public BookTheme theme() {
        if (this.theme == null) {
            this.theme = ThemeRegistry.createTheme(this.themeData);
        }
        return this.theme;
    }

    public boolean allowOpenBooksWithInvalidLinks() {
        return this.allowOpenBooksWithInvalidLinks;
    }

    public boolean showRecentlyUnlocked() {
        return this.showRecentlyUnlocked;
    }
}
