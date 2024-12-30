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
import com.klikli_dev.modonomicon.data.BookEntryJsonLoader;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Book {
    protected ResourceLocation id;
    protected String name;
    protected BookTextHolder description;
    protected String tooltip;
    protected String creativeTab;


    protected ResourceLocation model;
    protected ResourceLocation bookOverviewTexture;
    protected ResourceLocation frameTexture;
    protected BookFrameOverlay topFrameOverlay;
    protected BookFrameOverlay bottomFrameOverlay;
    protected BookFrameOverlay leftFrameOverlay;
    protected BookFrameOverlay rightFrameOverlay;
    protected ResourceLocation bookContentTexture;

    protected ResourceLocation craftingTexture;
    protected ResourceLocation turnPageSound;
    protected Map<ResourceLocation, BookCategory> categories;
    protected Map<ResourceLocation, BookEntry> entries;
    protected Map<ResourceLocation, BookCommand> commands;


    protected int defaultTitleColor;
    protected float categoryButtonIconScale;
    protected boolean autoAddReadConditions;
    protected boolean generateBookItem;
    @Nullable
    protected ResourceLocation customBookItem;

    protected ResourceLocation font;

    /**
     * The display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style)
     * If the book is in index mode then all categories will also be shown in index mode. If the book is in node mode, then individual categories can be in index mode.
     * If in index mode, the frame textures will be ignored, instead bookContentTexture will be used.
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

    protected ResourceLocation leafletEntry;

    protected PageDisplayMode pageDisplayMode = PageDisplayMode.DOUBLE_PAGE;
    protected ResourceLocation singlePageTexture = ResourceLocation.parse(Data.Book.DEFAULT_SINGLE_PAGE_TEXTURE);

    /**
     * If true, invalid links do not show an error screen when opening the book.
     * Instead, the book and pages will open, but the link will not work.
     */
    protected boolean allowOpenBooksWithInvalidLinks;

    /**
     * A map of macros. This is filled automatically based on LoaderRegistry#dynamicTextMacroLoaders, not loaded from JSON.
     */
    protected final Map<String, String> textMacros = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public Book(ResourceLocation id, String name, BookTextHolder description, String tooltip, ResourceLocation model, BookDisplayMode displayMode, boolean generateBookItem,
                @Nullable ResourceLocation customBookItem, String creativeTab, ResourceLocation font, ResourceLocation bookOverviewTexture, ResourceLocation frameTexture,
                BookFrameOverlay topFrameOverlay, BookFrameOverlay bottomFrameOverlay, BookFrameOverlay leftFrameOverlay, BookFrameOverlay rightFrameOverlay,
                ResourceLocation bookContentTexture, ResourceLocation craftingTexture, ResourceLocation turnPageSound,
                int defaultTitleColor, float categoryButtonIconScale, boolean autoAddReadConditions, int bookTextOffsetX, int bookTextOffsetY, int bookTextOffsetWidth, int bookTextOffsetHeight,
                int categoryButtonXOffset, int categoryButtonYOffset, int searchButtonXOffset, int searchButtonYOffset, int readAllButtonYOffset, ResourceLocation leafletEntry,
                PageDisplayMode pageDisplayMode, ResourceLocation singlePageTexture, boolean allowOpenBooksWithInvalidLinks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tooltip = tooltip;
        this.model = model;
        this.displayMode = displayMode;
        this.generateBookItem = generateBookItem;
        this.customBookItem = customBookItem;
        this.creativeTab = creativeTab;
        this.bookOverviewTexture = bookOverviewTexture;
        this.font = font;
        this.frameTexture = frameTexture;
        this.topFrameOverlay = topFrameOverlay;
        this.bottomFrameOverlay = bottomFrameOverlay;
        this.leftFrameOverlay = leftFrameOverlay;
        this.rightFrameOverlay = rightFrameOverlay;
        this.bookContentTexture = bookContentTexture;
        this.craftingTexture = craftingTexture;
        this.turnPageSound = turnPageSound;
        this.defaultTitleColor = defaultTitleColor;
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
    }

    public static Book fromJson(ResourceLocation id, JsonObject json, HolderLookup.Provider provider) {
        var name = GsonHelper.getAsString(json, "name");
        var description = BookGsonHelper.getAsBookTextHolder(json, "description", BookTextHolder.EMPTY, provider);
        var tooltip = GsonHelper.getAsString(json, "tooltip", "");
        var model = ResourceLocation.parse(GsonHelper.getAsString(json, "model", Data.Book.DEFAULT_MODEL));
        var generateBookItem = GsonHelper.getAsBoolean(json, "generate_book_item", true);
        var displayMode = BookDisplayMode.byName(GsonHelper.getAsString(json, "display_mode", BookDisplayMode.NODE.getSerializedName()));
        var customBookItem = json.has("custom_book_item") ?
                ResourceLocation.parse(GsonHelper.getAsString(json, "custom_book_item")) :
                null;
        var creativeTab = GsonHelper.getAsString(json, "creative_tab", "misc");
        var bookOverviewTexture = ResourceLocation.parse(GsonHelper.getAsString(json, "book_overview_texture", Data.Book.DEFAULT_OVERVIEW_TEXTURE));
        var frameTexture = ResourceLocation.parse(GsonHelper.getAsString(json, "frame_texture", Data.Book.DEFAULT_FRAME_TEXTURE));

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

        var font = ResourceLocation.parse(GsonHelper.getAsString(json, "font", Data.Book.DEFAULT_FONT));

        var bookContentTexture = ResourceLocation.parse(GsonHelper.getAsString(json, "book_content_texture", Data.Book.DEFAULT_CONTENT_TEXTURE));
        var craftingTexture = ResourceLocation.parse(GsonHelper.getAsString(json, "crafting_texture", Data.Book.DEFAULT_CRAFTING_TEXTURE));
        var turnPageSound = ResourceLocation.parse(GsonHelper.getAsString(json, "turn_page_sound", Data.Book.DEFAULT_PAGE_TURN_SOUND));
        var defaultTitleColor = GsonHelper.getAsInt(json, "default_title_color", 0x00000);
        var categoryButtonIconScale = GsonHelper.getAsFloat(json, "category_button_icon_scale", 1.0f);
        var autoAddReadConditions = GsonHelper.getAsBoolean(json, "auto_add_read_conditions", false);

        var bookTextOffsetX = GsonHelper.getAsInt(json, "book_text_offset_x", 0);
        var bookTextOffsetY = GsonHelper.getAsInt(json, "book_text_offset_y", 0);
        var bookTextOffsetWidth = GsonHelper.getAsInt(json, "book_text_offset_width", 0);
        var bookTextOffsetHeight = GsonHelper.getAsInt(json, "book_text_offset_height", 0);

        var categoryButtonXOffset = GsonHelper.getAsInt(json, "category_button_x_offset", 0);
        var categoryButtonYOffset = GsonHelper.getAsInt(json, "category_button_y_offset", 0);
        var searchButtonXOffset = GsonHelper.getAsInt(json, "search_button_x_offset", 0);
        var searchButtonYOffset = GsonHelper.getAsInt(json, "search_button_y_offset", 0);
        var readAllButtonYOffset = GsonHelper.getAsInt(json, "read_all_button_y_offset", 0);

        ResourceLocation leafletEntry = null;
        if (json.has("leaflet_entry")) {
            var leafletEntryPath = GsonHelper.getAsString(json, "leaflet_entry");
            //leaflet entries can be without a namespace, in which case we use the book namespace.
            leafletEntry = leafletEntryPath.contains(":") ?
                    ResourceLocation.parse(leafletEntryPath) :
                    ResourceLocation.fromNamespaceAndPath(id.getNamespace(), leafletEntryPath);
        }

        var pageDisplayMode = PageDisplayMode.byName(GsonHelper.getAsString(json, "page_display_mode", PageDisplayMode.DOUBLE_PAGE.getSerializedName()));
        var singlePageTexture = ResourceLocation.parse(GsonHelper.getAsString(json, "single_page_texture", Data.Book.DEFAULT_SINGLE_PAGE_TEXTURE));

        var allowOpenBooksWithInvalidLinks = GsonHelper.getAsBoolean(json, "allow_open_book_with_invalid_links", false);

        return new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font, bookOverviewTexture,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, bookTextOffsetHeight,
                categoryButtonXOffset, categoryButtonYOffset, searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset, leafletEntry, pageDisplayMode, singlePageTexture, allowOpenBooksWithInvalidLinks);
    }


    @SuppressWarnings("deprecation")
    public static Book fromNetwork(ResourceLocation id, RegistryFriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var description = BookTextHolder.fromNetwork(buffer);
        var tooltip = buffer.readUtf();
        var model = buffer.readResourceLocation();
        var displayMode = BookDisplayMode.byId(buffer.readByte());

        var generateBookItem = buffer.readBoolean();
        var customBookItem = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        var creativeTab = buffer.readUtf();

        var font = buffer.readResourceLocation();

        var bookOverviewTexture = buffer.readResourceLocation();

        var frameTexture = buffer.readResourceLocation();

        var topFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var bottomFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var leftFrameOverlay = BookFrameOverlay.fromNetwork(buffer);
        var rightFrameOverlay = BookFrameOverlay.fromNetwork(buffer);

        var bookContentTexture = buffer.readResourceLocation();
        var craftingTexture = buffer.readResourceLocation();
        var turnPageSound = buffer.readResourceLocation();
        var defaultTitleColor = buffer.readInt();
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

        var leafletEntry = buffer.readNullable(FriendlyByteBuf::readResourceLocation);

        var pageDisplayMode = PageDisplayMode.byId(buffer.readByte());
        var singlePageTexture = buffer.readResourceLocation();

        var allowOpenBooksWithInvalidLinks = buffer.readBoolean();

        var textMacros = buffer.readMap((b) -> b.readUtf(), (b) -> b.readUtf()); //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#readMap
        var book = new Book(id, name, description, tooltip, model, displayMode, generateBookItem, customBookItem, creativeTab, font, bookOverviewTexture,
                frameTexture, topFrameOverlay, bottomFrameOverlay, leftFrameOverlay, rightFrameOverlay,
                bookContentTexture, craftingTexture, turnPageSound, defaultTitleColor, categoryButtonIconScale, autoAddReadConditions, bookTextOffsetX, bookTextOffsetY, bookTextOffsetWidth, bookTextOffsetHeight,
                categoryButtonXOffset, categoryButtonYOffset, searchButtonXOffset, searchButtonYOffset, readAllButtonYOffset, leafletEntry, pageDisplayMode, singlePageTexture, allowOpenBooksWithInvalidLinks);

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
        textMacros.put(key, value);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        this.description.toNetwork(buffer);
        buffer.writeUtf(this.tooltip);
        buffer.writeResourceLocation(this.model);
        buffer.writeByte(this.displayMode.ordinal());

        buffer.writeBoolean(this.generateBookItem);

        buffer.writeNullable(this.customBookItem, FriendlyByteBuf::writeResourceLocation);

        buffer.writeUtf(this.creativeTab);

        buffer.writeResourceLocation(this.font);

        buffer.writeResourceLocation(this.bookOverviewTexture);
        buffer.writeResourceLocation(this.frameTexture);

        this.topFrameOverlay.toNetwork(buffer);
        this.bottomFrameOverlay.toNetwork(buffer);
        this.leftFrameOverlay.toNetwork(buffer);
        this.rightFrameOverlay.toNetwork(buffer);

        buffer.writeResourceLocation(this.bookContentTexture);
        buffer.writeResourceLocation(this.craftingTexture);
        buffer.writeResourceLocation(this.turnPageSound);
        buffer.writeInt(this.defaultTitleColor);
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

        buffer.writeNullable(this.leafletEntry, FriendlyByteBuf::writeResourceLocation);

        buffer.writeByte(this.pageDisplayMode.ordinal());
        buffer.writeResourceLocation(this.singlePageTexture);

        buffer.writeBoolean(this.allowOpenBooksWithInvalidLinks);
        buffer.writeMap(this.textMacros, (b, v) -> b.writeUtf(v), (b, v) -> b.writeUtf(v));  //necessary because using lambda causes ambiguous reference in Neo with their IFriendlyByteBufExtension#writeMap
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public ResourceLocation getTurnPageSound() {
        return this.turnPageSound;
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public float getCategoryButtonIconScale() {
        return this.categoryButtonIconScale;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Map<String, String> textMacros() {
        return textMacros;
    }

    public void addCategory(BookCategory category) {
        this.categories.putIfAbsent(category.id, category);
    }

    public BookCategory getCategory(ResourceLocation id) {
        return this.categories.get(id);
    }

    public Map<ResourceLocation, BookCategory> getCategories() {
        return this.categories;
    }

    public List<BookCategory> getCategoriesSorted() {
        return this.categories.values().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)).toList();
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.getId(), entry);
    }

    public BookEntry getEntry(ResourceLocation id) {
        return this.entries.get(id);
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addCommand(BookCommand command) {
        this.commands.putIfAbsent(command.id, command);
    }

    public Map<ResourceLocation, BookCommand> getCommands() {
        return this.commands;
    }

    public BookCommand getCommand(ResourceLocation id) {
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

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    public ResourceLocation getFont() {
        return this.font;
    }

    public ResourceLocation getFrameTexture() {
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
    public ResourceLocation getCustomBookItem() {
        return this.customBookItem;
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }

    public ResourceLocation getModel() {
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

    public ResourceLocation getLeafletEntry() {
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

    public ResourceLocation getSinglePageTexture() {
        return this.singlePageTexture;
    }

    public boolean allowOpenBooksWithInvalidLinks() {
        return this.allowOpenBooksWithInvalidLinks;
    }
}
