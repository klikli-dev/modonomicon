/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.ThemeRegistry;
import com.klikli_dev.modonomicon.data.BookEntryJsonLoader;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
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
    protected Identifier frameTexture;
    protected BookFrameOverlay topFrameOverlay;
    protected BookFrameOverlay bottomFrameOverlay;
    protected BookFrameOverlay leftFrameOverlay;
    protected BookFrameOverlay rightFrameOverlay;
    protected Identifier craftingTexture;
    protected Identifier turnPageSound;
    protected Map<Identifier, BookCategory> categories;
    protected Map<Identifier, BookEntry> entries;
    protected Map<Identifier, BookCommand> commands;


    protected int defaultTitleColor;
    protected int defaultTextColor;
    protected float categoryButtonIconScale;
    protected boolean autoAddReadConditions;
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

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth;

    /**
     * When rendering book text holders, add this offset to the height (allows to create a bottom margin)
     * To make the bottom end of the text move up (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetHeight;

    protected int categoryButtonXOffset;
    protected int categoryButtonYOffset;
    protected int searchButtonXOffset;
    protected int searchButtonYOffset;
    protected int readAllButtonYOffset;

    protected Identifier leafletEntry;

    protected PageDisplayMode pageDisplayMode = PageDisplayMode.DOUBLE_PAGE;
    protected Identifier singlePageTexture = Identifier.parse(Data.Book.DEFAULT_SINGLE_PAGE_TEXTURE);

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
     * A map of macros. This is filled automatically based on LoaderRegistry#dynamicTextMacroLoaders, not loaded from JSON.
     */
    protected final Map<String, String> textMacros = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public Book(Identifier id, String name, BookTextHolder description, String tooltip, Identifier model, BookDisplayMode displayMode, boolean generateBookItem,
                @Nullable Identifier customBookItem, String creativeTab, Identifier font, Identifier frameTexture,
                BookFrameOverlay topFrameOverlay, BookFrameOverlay bottomFrameOverlay, BookFrameOverlay leftFrameOverlay, BookFrameOverlay rightFrameOverlay,
                Identifier craftingTexture, Identifier turnPageSound,
                int defaultTitleColor, int defaultTextColor, float categoryButtonIconScale, boolean autoAddReadConditions, int bookTextOffsetX, int bookTextOffsetY, int bookTextOffsetWidth, int bookTextOffsetHeight,
                int categoryButtonXOffset, int categoryButtonYOffset, int searchButtonXOffset, int searchButtonYOffset, int readAllButtonYOffset, Identifier leafletEntry,
                PageDisplayMode pageDisplayMode, Identifier singlePageTexture, boolean allowOpenBooksWithInvalidLinks, boolean showRecentlyUnlocked,
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
        this.frameTexture = frameTexture;
        this.topFrameOverlay = topFrameOverlay;
        this.bottomFrameOverlay = bottomFrameOverlay;
        this.leftFrameOverlay = leftFrameOverlay;
        this.rightFrameOverlay = rightFrameOverlay;
        this.craftingTexture = craftingTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
        this.defaultTextColor = defaultTextColor;
        this.categoryButtonIconScale = categoryButtonIconScale;
        this.autoAddReadConditions = autoAddReadConditions;
        this.categories = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.entries = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.commands = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.bookTextOffsetX = bookTextOffsetX;
        this.bookTextOffsetY = bookTextOffsetY;
        this.bookTextOffsetWidth = bookTextOffsetWidth;
        this.bookTextOffsetHeight = bookTextOffsetHeight;

        this.categoryButtonXOffset = categoryButtonXOffset;
        this.categoryButtonYOffset = categoryButtonYOffset;
        this.searchButtonXOffset = searchButtonXOffset;
        this.searchButtonYOffset = searchButtonYOffset;
        this.readAllButtonYOffset = readAllButtonYOffset;

        this.leafletEntry = leafletEntry;

        this.pageDisplayMode = pageDisplayMode;
        this.singlePageTexture = singlePageTexture;

        this.allowOpenBooksWithInvalidLinks = allowOpenBooksWithInvalidLinks;

        this.showRecentlyUnlocked = showRecentlyUnlocked;
        this.themeData = themeData;
    }

    public static Book fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return fromJson(id, json, BookThemeData.fromLegacyBookJson(json), provider);
    }

    public static Book fromJson(Identifier id, JsonObject json, BookThemeData themeData, HolderLookup.Provider provider) {
        var name = GsonHelper.getAsString(json, "name");
        var description = BookGsonHelper.getAsBookTextHolder(json, "description", BookTextHolder.EMPTY, provider);
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = Identifier.parse(GsonHelper.getAsString(json, "model", Data.Book.DEFAULT_MODEL));
        var generateBookItem = GsonHelper.getAsBoolean(json, "generate_book_item", true);
        var displayMode = BookDisplayMode.byName(GsonHelper.getAsString(json, "display_mode", BookDisplayMode.NODE.getSerializedName()));
        var customBookItem = json.has("custom_book_item") ?
                Identifier.parse(GsonHelper.getAsString(json, "custom_book_item")) :
                null;
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");
        var frameTexture = Identifier.parse(GsonHelper.getAsString(json, "frame_texture", Data.Book.DEFAULT_FRAME_TEXTURE));

        var topFrameOverlay = json.has("top_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("top_frame_overlay").getAsJsonObject()) :
                Data.Book.DEFAULT_TOP_FRAME_OVERLAY;

        var bottomFrameOverlay = json.has("bottom_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("bottom_frame_overlay").getAsJsonObject()) :
                Data.Book.DEFAULT_BOTTOM_FRAME_OVERLAY;

        var leftFrameOverlay = json.has("left_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("left_frame_overlay").getAsJsonObject()) :
                Data.Book.DEFAULT_LEFT_FRAME_OVERLAY;

        var rightFrameOverlay = json.has("right_frame_overlay") ?
                BookFrameOverlay.fromJson(json.get("right_frame_overlay").getAsJsonObject()) :
                Data.Book.DEFAULT_RIGHT_FRAME_OVERLAY;

        var font = Identifier.parse(GsonHelper.getAsString(json, "font", Data.Book.DEFAULT_FONT));

        var craftingTexture = Identifier.parse(GsonHelper.getAsString(json, "crafting_texture", Data.Book.DEFAULT_CRAFTING_TEXTURE));
        var turnPageSound = Identifier.parse(GsonHelper.getAsString(json, "turn_page_sound", Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = themeData.palette().defaultTitleColor();
        var defaultTextColor = themeData.palette().defaultTextColor();
        var categoryButtonIconScale = themeData.layout().categoryButtonIconScale();
        var autoAddReadConditions = GsonHelper.getAsBoolean(json, "auto_add_read_conditions", false);

        var bookTextOffsetX = themeData.layout().bookTextOffsetX();
        var bookTextOffsetY = themeData.layout().bookTextOffsetY();
        var bookTextOffsetWidth = themeData.layout().bookTextOffsetWidth();
        var bookTextOffsetHeight = themeData.layout().bookTextOffsetHeight();

        var categoryButtonXOffset = themeData.layout().categoryButtonXOffset();
        var categoryButtonYOffset = themeData.layout().categoryButtonYOffset();
        var searchButtonXOffset = themeData.layout().searchButtonXOffset();
        var searchButtonYOffset = themeData.layout().searchButtonYOffset();
        var readAllButtonYOffset = themeData.layout().readAllButtonYOffset();

        Identifier leafletEntry = null;
        if (json.has("leaflet_entry")) {
            var leafletEntryPath = GsonHelper.getAsString(json, "leaflet_entry");
            //leaflet entries can be without a namespace, in which case we use the book namespace.
            leafletEntry = leafletEntryPath.contains(":") ?
                    Identifier.parse(leafletEntryPath) :
                    Identifier.fromNamespaceAndPath(id.getNamespace(), leafletEntryPath);
        }

        var pageDisplayMode = PageDisplayMode.byName(GsonHelper.getAsString(json, "page_display_mode", PageDisplayMode.DOUBLE_PAGE.getSerializedName()));
        var singlePageTexture = Identifier.parse(GsonHelper.getAsString(json, "single_page_texture", Data.Book.DEFAULT_SINGLE_PAGE_TEXTURE));

        var allowOpenBooksWithInvalidLinks = GsonHelper.getAsBoolean(json, "allow_open_book_with_invalid_links", false);

        var showRecentlyUnlocked = GsonHelper.getAsBoolean(json, "show_recently_unlocked", true);

        return new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                craftingTexture, turnPageSound, defaultTitleColor, defaultTextColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, bookTextOffsetHeight,
                categoryButtonXOffset, categoryButtonYOffset, searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset, leafletEntry, pageDisplayMode, singlePageTexture, allowOpenBooksWithInvalidLinks, showRecentlyUnlocked, themeData);
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

        var frameTexture = buffer.readIdentifier();

        var topFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var bottomFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var leftFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var rightFrameOverlay = BookFrameOverlay.fromNetwork(buffer);

        var craftingTexture = buffer.readIdentifier();
        var turnPageSound = buffer.readIdentifier();
        var defaultTitleColor = buffer.readInt();
        var defaultTextColor = buffer.readInt();
        var categoryButtonIconScale = buffer.readFloat();
        var autoAddReadConditions = buffer.readBoolean();
        var bookTextOffsetX = (int) buffer.readShort();
        var bookTextOffsetY = (int) buffer.readShort();
        var bookTextOffsetWidth = (int) buffer.readShort();
        var bookTextOffsetHeight = (int) buffer.readShort();

        var categoryButtonXOffset = (int) buffer.readShort();
        var categoryButtonYOffset = (int) buffer.readShort();
        var searchButtonXOffset = (int) buffer.readShort();
        var searchButtonYOffset = (int) buffer.readShort();
        var readAllButtonYOffset = (int) buffer.readShort();

        var leafletEntry = buffer.readNullable(FriendlyByteBuf::readIdentifier);

        var pageDisplayMode = PageDisplayMode.byId(buffer.readByte());
        var singlePageTexture = buffer.readIdentifier();

        var allowOpenBooksWithInvalidLinks = buffer.readBoolean();

        var showRecentlyUnlocked = buffer.readBoolean();

        var textMacros = buffer.readMap((b) -> b.readUtf(), (b) -> b.readUtf()); //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#readMap
        var book = new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                craftingTexture, turnPageSound, defaultTitleColor, defaultTextColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, bookTextOffsetHeight,
                categoryButtonXOffset, categoryButtonYOffset, searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset, leafletEntry, pageDisplayMode, singlePageTexture, allowOpenBooksWithInvalidLinks, showRecentlyUnlocked, themeData);

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

        buffer.writeIdentifier(this.frameTexture);

        this.topFrameOverlay.toNetwork(buffer);
        this.bottomFrameOverlay.toNetwork(buffer);
        this.leftFrameOverlay.toNetwork(buffer);
        this.rightFrameOverlay.toNetwork(buffer);

        buffer.writeIdentifier(this.craftingTexture);
        buffer.writeIdentifier(this.turnPageSound);
        buffer.writeInt(this.defaultTitleColor);
        buffer.writeInt(this.defaultTextColor);
        buffer.writeFloat(this.categoryButtonIconScale);
        buffer.writeBoolean(this.autoAddReadConditions);

        buffer.writeShort(this.bookTextOffsetX);
        buffer.writeShort(this.bookTextOffsetY);
        buffer.writeShort(this.bookTextOffsetWidth);
        buffer.writeShort(this.bookTextOffsetHeight);

        buffer.writeShort(this.categoryButtonXOffset);
        buffer.writeShort(this.categoryButtonYOffset);
        buffer.writeShort(this.searchButtonXOffset);
        buffer.writeShort(this.searchButtonYOffset);
        buffer.writeShort(this.readAllButtonYOffset);

        buffer.writeNullable(this.leafletEntry, FriendlyByteBuf::writeIdentifier);

        buffer.writeByte(this.pageDisplayMode.ordinal());
        buffer.writeIdentifier(this.singlePageTexture);

        buffer.writeBoolean(this.allowOpenBooksWithInvalidLinks);
        buffer.writeBoolean(this.showRecentlyUnlocked);
        buffer.writeMap(this.textMacros, (b, v) -> b.writeUtf(v), (b, v) -> b.writeUtf(v));  //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#writeMap
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public Identifier getTurnPageSound() {
        return this.turnPageSound;
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public int getDefaultTextColor() {
        return this.defaultTextColor;
    }

    public float getCategoryButtonIconScale() {
        return this.categoryButtonIconScale;
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

    public Identifier getFrameTexture() {
        return this.frameTexture;
    }

    public BookFrameOverlay getTopFrameOverlay() {
        return this.topFrameOverlay;
    }

    public BookFrameOverlay getBottomFrameOverlay() {
        return this.bottomFrameOverlay;
    }

    public BookFrameOverlay getLeftFrameOverlay() {
        return this.leftFrameOverlay;
    }

    public BookFrameOverlay getRightFrameOverlay() {
        return this.rightFrameOverlay;
    }

    @Nullable
    public Identifier getCustomBookItem() {
        return this.customBookItem;
    }

    public Identifier getCraftingTexture() {
        return this.craftingTexture;
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

    public int getBookTextOffsetX() {
        return this.bookTextOffsetX;
    }

    public int getBookTextOffsetY() {
        return this.bookTextOffsetY;
    }

    public int getBookTextOffsetWidth() {
        return this.bookTextOffsetWidth;
    }

    public int getBookTextOffsetHeight() {
        return this.bookTextOffsetHeight;
    }

    public int getCategoryButtonXOffset() {
        return this.categoryButtonXOffset;
    }

    public int getCategoryButtonYOffset() {
        return this.categoryButtonYOffset;
    }

    public int getSearchButtonXOffset() {
        return this.searchButtonXOffset;
    }

    public int getSearchButtonYOffset() {
        return this.searchButtonYOffset;
    }

    public int getReadAllButtonYOffset() {
        return this.readAllButtonYOffset;
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

    public Identifier getSinglePageTexture() {
        return this.singlePageTexture;
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
