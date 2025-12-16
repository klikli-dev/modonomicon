/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Category;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class BookCategory {

    protected Identifier id;
    protected Book book;
    protected String name;
    protected BookIcon icon;
    protected BookTextHolder description;
    /**
     * The display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style)
     */
    protected BookDisplayMode displayMode;
    protected int sortNumber;
    protected Identifier background;
    protected int backgroundWidth;
    protected int backgroundHeight;
    protected int maxScrollX;
    protected int maxScrollY;
    /**
     * Allows to modify how "zoomed in" the background texture is rendered.
     * A lower value means the texture is zoomed OUT more -> it is sharper / less blurry.
     */
    protected float backgroundTextureZoomMultiplier;
    protected List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers;
    protected Identifier entryTextures;
    protected Map<Identifier, BookEntry> entries;
    protected BookCondition condition;
    protected boolean showCategoryButton;
    /**
     * The entry to open when this category is opened.
     * If null, no entry will be opened.
     */
    protected Identifier entryToOpen;
    /**
     * If true, the entryToOpen will only be opened the first time the category is opened.
     * If false, the entryToOpen will be opened every time the category is opened.
     */
    protected boolean openEntryToOpenOnlyOnce;

    public BookCategory(Identifier id, String name, BookTextHolder description, int sortNumber, BookCondition condition, boolean showCategoryButton, BookIcon icon, BookDisplayMode displayMode, Identifier background, int backgroundWidth, int backgroundHeight, int maxScrollX, int maxScrollY, float backgroundTextureZoomMultiplier, List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers, Identifier entryTextures, Identifier entryToOpen, boolean openEntryOnlyOnce) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sortNumber = sortNumber;
        this.condition = condition;
        this.showCategoryButton = showCategoryButton;
        this.icon = icon;
        this.displayMode = displayMode;
        this.background = background;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.maxScrollX = maxScrollX;
        this.maxScrollY = maxScrollY;
        this.backgroundTextureZoomMultiplier = backgroundTextureZoomMultiplier;
        this.backgroundParallaxLayers = backgroundParallaxLayers;
        this.entryTextures = entryTextures;
        this.entries = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        this.entryToOpen = entryToOpen;
        this.openEntryToOpenOnlyOnce = openEntryOnlyOnce;
    }

    public static BookCategory fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        var name = GsonHelper.getAsString(json, "name");
        var description = BookGsonHelper.getAsBookTextHolder(json, "description", BookTextHolder.EMPTY, provider);
        var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);
        var icon = BookIcon.fromJson(json.get("icon"), provider);
        var displayMode = BookDisplayMode.byName(GsonHelper.getAsString(json, "display_mode", BookDisplayMode.NODE.getSerializedName()));
        var background = Identifier.parse(GsonHelper.getAsString(json, "background", Category.DEFAULT_BACKGROUND));
        var backgroundWidth = GsonHelper.getAsInt(json, "background_width", Category.DEFAULT_BACKGROUND_WIDTH);
        var backgroundHeight = GsonHelper.getAsInt(json, "background_height", Category.DEFAULT_BACKGROUND_HEIGHT);
        var defaultMaxScrollX = GsonHelper.getAsInt(json, "max_scroll_x", Category.DEFAULT_MAX_SCROLL_X);
        var defaultMaxScrollY = GsonHelper.getAsInt(json, "max_scroll_y", Category.DEFAULT_MAX_SCROLL_Y);

        var backgroundTextureZoomMultiplier = GsonHelper.getAsFloat(json, "background_texture_zoom_multiplier", Category.DEFAULT_BACKGROUND_TEXTURE_ZOOM_MULTIPLIER);
        var entryTextures = Identifier.parse(GsonHelper.getAsString(json, "entry_textures", Category.DEFAULT_ENTRY_TEXTURES));
        var showCategoryButton = GsonHelper.getAsBoolean(json, "show_category_button", true);

        BookCondition condition = new BookNoneCondition(); //default to unlocked
        if (json.has("condition")) {
            condition = BookCondition.fromJson(id, json.getAsJsonObject("condition"), provider);
        }

        List<BookCategoryBackgroundParallaxLayer> backgroundParallaxLayers = List.of();
        if (json.has("background_parallax_layers"))
            backgroundParallaxLayers = BookCategoryBackgroundParallaxLayer.fromJson(json.getAsJsonArray("background_parallax_layers"));

        Identifier entryToOpen = null;
        if (json.has("entry_to_open")) {
            var entryToOpenPath = GsonHelper.getAsString(json, "entry_to_open");
            entryToOpen = entryToOpenPath.contains(":") ?
                    Identifier.parse(entryToOpenPath) :
                    Identifier.fromNamespaceAndPath(id.getNamespace(), entryToOpenPath);
        }
        boolean openEntryOnlyOnce = GsonHelper.getAsBoolean(json, "open_entry_to_open_only_once", true);

        return new BookCategory(id, name, description, sortNumber, condition, showCategoryButton, icon, displayMode, background, backgroundWidth, backgroundHeight,
                defaultMaxScrollX, defaultMaxScrollY, backgroundTextureZoomMultiplier, backgroundParallaxLayers, entryTextures, entryToOpen, openEntryOnlyOnce);
    }

    public static BookCategory fromNetwork(Identifier id, RegistryFriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        var description = BookTextHolder.fromNetwork(buffer);
        var sortNumber = buffer.readInt();
        var icon = BookIcon.fromNetwork(buffer);
        var displayMode = BookDisplayMode.byId(buffer.readByte());
        var background = buffer.readIdentifier();
        var backgroundWidth = buffer.readVarInt();
        var backgroundHeight = buffer.readVarInt();
        var defaultMaxScrollX = buffer.readVarInt();
        var defaultMaxScrollY = buffer.readVarInt();
        var backgroundTextureZoomMultiplier = buffer.readFloat();
        var backgroundParallaxLayers = buffer.readList(BookCategoryBackgroundParallaxLayer::fromNetwork);
        var entryTextures = buffer.readIdentifier();
        var condition = BookCondition.fromNetwork(buffer);
        var showCategoryButton = buffer.readBoolean();
        var entryToOpen = buffer.readNullable(FriendlyByteBuf::readResourceLocation);
        var openEntryOnlyOnce = buffer.readBoolean();
        return new BookCategory(id, name, description, sortNumber, condition, showCategoryButton, icon, displayMode, background, backgroundWidth, backgroundHeight,
                defaultMaxScrollX, defaultMaxScrollY, backgroundTextureZoomMultiplier, backgroundParallaxLayers, entryTextures, entryToOpen, openEntryOnlyOnce);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        this.description.toNetwork(buffer);
        buffer.writeInt(this.sortNumber);
        this.icon.toNetwork(buffer);
        buffer.writeByte(this.displayMode.ordinal());
        buffer.writeIdentifier(this.background);
        buffer.writeVarInt(this.backgroundWidth);
        buffer.writeVarInt(this.backgroundHeight);
        buffer.writeVarInt(this.maxScrollX);
        buffer.writeVarInt(this.maxScrollY);
        buffer.writeFloat(this.backgroundTextureZoomMultiplier);
        buffer.writeCollection(this.backgroundParallaxLayers, (buf, layer) -> layer.toNetwork(buf));
        buffer.writeIdentifier(this.entryTextures);
        BookCondition.toNetwork(this.condition, buffer);
        buffer.writeBoolean(this.showCategoryButton);
        buffer.writeNullable(this.entryToOpen, FriendlyByteBuf::writeResourceLocation);
        buffer.writeBoolean(this.openEntryToOpenOnlyOnce);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Level level, Book book) {
        this.book = book;

        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            entry.build(level, this);
            BookErrorManager.get().getContextHelper().entryId = null;
        }

        if (this.entryToOpen != null) {
            var entry = this.entries.get(this.entryToOpen);
            if (entry == null) { //entry must exist in category!
                BookErrorManager.get().error(MessageFormat.format("EntryToOpen \"{0}\" in Category \"{1}\" does not exist.", this.entryToOpen, this.getId()));
            }
        }
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        if (!this.description.hasComponent()) {
            this.description = new RenderedBookTextHolder(this.description, textRenderer.render(this.description.getString()));
        }

        for (var entry : this.entries.values()) {
            BookErrorManager.get().getContextHelper().entryId = entry.getId();
            try {
                entry.prerenderMarkdown(textRenderer);
            } catch (Exception e) {
                BookErrorManager.get().error("Failed to render markdown in book '" + this.book.getId() + "' for entry '" + entry.getId() + "'", e);
            }

            BookErrorManager.get().getContextHelper().entryId = null;
        }
    }

    public Identifier getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getName() {
        return this.name;
    }

    public BookTextHolder getDescription() {
        return this.description;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public BookDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public Identifier getBackground() {
        return this.background;
    }

    public int getBackgroundWidth() {
        return this.backgroundWidth;
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public int getMaxScrollX() {
        return this.maxScrollX;
    }

    public int getMaxScrollY() {
        return this.maxScrollY;
    }

    public float getBackgroundTextureZoomMultiplier() {
        return this.backgroundTextureZoomMultiplier;
    }

    public List<BookCategoryBackgroundParallaxLayer> getBackgroundParallaxLayers() {
        return this.backgroundParallaxLayers;
    }

    public Identifier getEntryTextures() {
        return this.entryTextures;
    }

    public Map<Identifier, BookEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.getId(), entry);
    }

    public BookEntry getEntry(Identifier id) {
        return this.entries.get(id);
    }

    public BookCondition getCondition() {
        return this.condition;
    }


    public boolean openEntryToOpenOnlyOnce() {
        return this.openEntryToOpenOnlyOnce;
    }

    public Identifier getEntryToOpen() {
        return this.entryToOpen;
    }

    public boolean showCategoryButton() {
        return this.showCategoryButton;
    }
}
