/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Book;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.book.BookFrameOverlay;
import com.klikli_dev.modonomicon.book.PageDisplayMode;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookModel {

    /**
     * The book ID, e.g. "modonomicon:demo". The ID must be unique (usually that is guaranteed by the mod ID).
     */
    protected ResourceLocation id;
    /**
     * The name of the book, should be a translation key/description id
     */
    protected String name;
    /**
     * The description to (optionally) display on the first page of the category.
     */
    protected BookTextHolderModel description = new BookTextHolderModel("");
    /**
     * The tooltip to optionally display on the book item on hover.
     */
    protected String tooltip = "";

    /**
     * The display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style)
     * If the book is in index mode then all categories will also be shown in index mode. If the book is in node mode, then individual categories can be in index mode.
     * If in index mode, the frame textures will be ignored, instead bookContentTexture will be used.
     */
    protected BookDisplayMode displayMode = BookDisplayMode.NODE;

    /**
     * The creative tab to add the book to.
     */
    protected ResourceLocation creativeTab = ResourceLocation.parse("modonomicon:modonomicon");

    /**
     * If true, automatically generates an item for this book and registers it with the creative tab.
     */
    protected boolean generateBookItem = true;

    /**
     * The item model to use for the book. Only used if generateBookItem = true.
     */
    protected ResourceLocation model = ResourceLocation.parse(Book.DEFAULT_MODEL);

    /**
     * If set, uses this item for the book. That means you need to implement all functionality to open the book yourself.
     */
    @Nullable
    protected ResourceLocation customBookItem = null;

    /**
     * This texture contains buttons for the "node view" of the book. E.g. search button, category buttons, "read all" button.
     */
    protected ResourceLocation bookOverviewTexture = ResourceLocation.parse(Data.Book.DEFAULT_OVERVIEW_TEXTURE);

    /**
     * The font to use for the book text.
     */
    protected ResourceLocation font = ResourceLocation.parse(Book.DEFAULT_FONT);

    protected ResourceLocation frameTexture = ResourceLocation.parse(Book.DEFAULT_FRAME_TEXTURE);
    protected BookFrameOverlay topFrameOverlay = Data.Book.DEFAULT_TOP_FRAME_OVERLAY;
    protected BookFrameOverlay bottomFrameOverlay = Data.Book.DEFAULT_BOTTOM_FRAME_OVERLAY;
    protected BookFrameOverlay leftFrameOverlay = Data.Book.DEFAULT_LEFT_FRAME_OVERLAY;
    protected BookFrameOverlay rightFrameOverlay = Data.Book.DEFAULT_RIGHT_FRAME_OVERLAY;

    /**
     * Contains textures for the entry view, as well as index views (book or category in index mode, as well as search screen).
     * This includes the book "page" background for double page view and various navigation buttons.
     */
    protected ResourceLocation bookContentTexture = ResourceLocation.parse(Data.Book.DEFAULT_CONTENT_TEXTURE);

    protected PageDisplayMode pageDisplayMode = PageDisplayMode.DOUBLE_PAGE;
    protected ResourceLocation singlePageTexture = ResourceLocation.parse(Data.Book.DEFAULT_SINGLE_PAGE_TEXTURE);

    /**
     * Contains textures for the crafting pages, such as crafting grids and result arrows.
     */
    protected ResourceLocation craftingTexture = ResourceLocation.parse(Book.DEFAULT_CRAFTING_TEXTURE);
    protected ResourceLocation turnPageSound = ResourceLocation.parse(Book.DEFAULT_PAGE_TURN_SOUND);
    protected int defaultTitleColor = 0x00000;
    protected float categoryButtonIconScale = 1.0f;

    protected List<BookCategoryModel> categories = new ArrayList<>();
    protected List<BookCommandModel> commands = new ArrayList<>();

    protected boolean autoAddReadConditions = false;


    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    protected int bookTextOffsetX = 0;

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    protected int bookTextOffsetY = 0;

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetWidth = 0;

    /**
     * When rendering book text holders, add this offset to the height (allows to create a bottom margin)
     * To make the bottom end of the text move up (as it would for a margin setting in eg css), use a negative value.
     */
    protected int bookTextOffsetHeight = 0;

    protected int categoryButtonXOffset = 0;
    protected int categoryButtonYOffset = 0;
    protected int searchButtonXOffset = 0;
    protected int searchButtonYOffset = 0;
    protected int readAllButtonYOffset = 0;

    /**
     * If this entry is set the book will ignore all other content and just display this entry.
     * Note that the entry still needs to be in a valid category, even if the category is not displayed.
     *
     * The book will be treated as a book in index mode (that means, no big "node view" background will be shown behind the entry).
     */
    @Nullable
    protected ResourceLocation leafletEntry;

    /**
     * If true, invalid links do not show an error screen when opening the book.
     * Instead, the book and pages will open, but the link will not work.
     */
    protected boolean allowOpenBooksWithInvalidLinks = false;

    /**
     * If true, this model will not generate a book.json file, but the categories and entries will still be generated.
     */
    protected boolean dontGenerateJson = false;

    protected BookModel(ResourceLocation id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id   The book ID, e.g. "modonomicon:demo". The ID must be unique (usually that is guaranteed by the mod ID).
     * @param name Should be a translation key.
     */
    public static BookModel create(ResourceLocation id, String name) {
        return new BookModel(id, name);
    }

    public boolean autoAddReadConditions() {
        return this.autoAddReadConditions;
    }

    public ResourceLocation getCraftingTexture() {
        return this.craftingTexture;
    }

    public ResourceLocation getTurnPageSound() {
        return this.turnPageSound;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    @Nullable
    public ResourceLocation getCustomBookItem() {
        return this.customBookItem;
    }

    public List<BookCategoryModel> getCategories() {
        return this.categories;
    }

    public List<BookCommandModel> getCommands() {
        return this.commands;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BookTextHolderModel getDescription() {
        return this.description;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public ResourceLocation getCreativeTab() {
        return this.creativeTab;
    }

    public ResourceLocation getModel() {
        return this.model;
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

    public ResourceLocation getBookContentTexture() {
        return this.bookContentTexture;
    }

    public int getDefaultTitleColor() {
        return this.defaultTitleColor;
    }

    public float getCategoryButtonIconScale() {
        return this.categoryButtonIconScale;
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

    public BookDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public PageDisplayMode getPageDisplayMode() {
        return this.pageDisplayMode;
    }

    public ResourceLocation getSinglePageTexture() {
        return this.singlePageTexture;
    }

    public @Nullable ResourceLocation getLeafletEntry() {
        return this.leafletEntry;
    }

    public boolean allowOpenBooksWithInvalidLinks() {
        return this.allowOpenBooksWithInvalidLinks;
    }

    public boolean dontGenerateJson() {
        return this.dontGenerateJson;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.add("description", this.description.toJson(provider));
        json.addProperty("tooltip", this.tooltip);
        json.addProperty("model", this.model.toString());
        json.addProperty("display_mode", this.displayMode.getSerializedName());
        json.addProperty("creative_tab", this.creativeTab.toString());
        json.addProperty("book_overview_texture", this.bookOverviewTexture.toString());
        json.addProperty("font", this.font.toString());
        json.addProperty("frame_texture", this.frameTexture.toString());
        json.add("top_frame_overlay", BookFrameOverlay.CODEC.encodeStart(JsonOps.INSTANCE, this.topFrameOverlay).getOrThrow());
        json.add("bottom_frame_overlay", BookFrameOverlay.CODEC.encodeStart(JsonOps.INSTANCE, this.bottomFrameOverlay).getOrThrow());
        json.add("left_frame_overlay", BookFrameOverlay.CODEC.encodeStart(JsonOps.INSTANCE, this.leftFrameOverlay).getOrThrow());
        json.add("right_frame_overlay", BookFrameOverlay.CODEC.encodeStart(JsonOps.INSTANCE, this.rightFrameOverlay).getOrThrow());
        json.addProperty("book_content_texture", this.bookContentTexture.toString());
        json.addProperty("crafting_texture", this.craftingTexture.toString());
        json.addProperty("turn_page_sound", this.turnPageSound.toString());
        json.addProperty("default_title_color", this.defaultTitleColor);
        json.addProperty("category_button_icon_scale", this.categoryButtonIconScale);
        json.addProperty("book_text_offset_x", this.bookTextOffsetX);
        json.addProperty("book_text_offset_y", this.bookTextOffsetY);
        json.addProperty("book_text_offset_width", this.bookTextOffsetWidth);
        json.addProperty("book_text_offset_height", this.bookTextOffsetHeight);
        json.addProperty("category_button_x_offset", this.categoryButtonXOffset);
        json.addProperty("category_button_y_offset", this.categoryButtonYOffset);
        json.addProperty("search_button_x_offset", this.searchButtonXOffset);
        json.addProperty("search_button_y_offset", this.searchButtonYOffset);
        json.addProperty("read_all_button_y_offset", this.readAllButtonYOffset);

        json.addProperty("auto_add_read_conditions", this.autoAddReadConditions);
        json.addProperty("generate_book_item", this.generateBookItem);
        if (this.customBookItem != null) {
            json.addProperty("custom_book_item", this.customBookItem.toString());
        }

        if (this.leafletEntry != null) {
            //if we are in the same namespace, which we basically always should be, omit namespace
            if (this.leafletEntry.getNamespace().equals(this.getId().getNamespace()))
                json.addProperty("leaflet_entry", this.leafletEntry.getPath());
            else
                json.addProperty("leaflet_entry", this.leafletEntry.toString());
        }

        json.addProperty("page_display_mode", this.pageDisplayMode.getSerializedName());
        json.addProperty("single_page_texture", this.singlePageTexture.toString());

        json.addProperty("allow_open_book_with_invalid_links", this.allowOpenBooksWithInvalidLinks);

        return json;
    }

    /**
     * The description to (optionally) display on the first page of the category.
     */
    public BookModel withDescription(String title) {
        this.description = new BookTextHolderModel(title);
        return this;
    }

    /**
     * The description to (optionally) display on the first page of the category.
     */
    public BookModel withDescription(Component title) {
        this.description = new BookTextHolderModel(title);
        return this;
    }

    public BookModel withTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public BookModel withCreativeTab(ResourceLocation creativeTab) {
        this.creativeTab = creativeTab;
        return this;
    }

    public BookModel withBookOverviewTexture(ResourceLocation bookOverviewTexture) {
        this.bookOverviewTexture = bookOverviewTexture;
        return this;
    }

    public BookModel withFont(ResourceLocation font) {
        this.font = font;
        return this;
    }

    /**
     * Sets the image to use as a frame texture.
     * Please note that the center of each side will be repeated/stretched to fit the screen size.
     * Default is {@link Data.Book#DEFAULT_FRAME_TEXTURE}.
     * See {@link BookModel#withBottomFrameOverride}, {@link BookModel#withTopFrameOverride}, {@link BookModel#withLeftFrameOverride} and  {@link BookModel#withRightFrameOverride} on how to set a non-repeating center part.
     */
    public BookModel withFrameTexture(ResourceLocation frameTexture) {
        this.frameTexture = frameTexture;
        return this;
    }

    /**
     * Sets the image to use as an overlay for the bottom frame.
     * This can either be just a copy of the same portion of the frame texture, or a different texture to show some unique details that are not repeated.
     * Default is {@link Data.Book#DEFAULT_BOTTOM_FRAME_OVERLAY}.
     */
    public BookModel withBottomFrameOverride(BookFrameOverlay overlay) {
        this.bottomFrameOverlay = overlay;
        return this;
    }

    /**
     * Sets the image to use as an overlay for the top frame.
     * This can either be just a copy of the same portion of the frame texture, or a different texture to show some unique details that are not repeated.
     * Default is {@link Data.Book#DEFAULT_TOP_FRAME_OVERLAY}.
     */
    public BookModel withTopFrameOverride(BookFrameOverlay overlay) {
        this.topFrameOverlay = overlay;
        return this;
    }

    /**
     * Sets the image to use as an overlay for the left frame.
     * This can either be just a copy of the same portion of the frame texture, or a different texture to show some unique details that are not repeated.
     * Default is {@link Data.Book#DEFAULT_LEFT_FRAME_OVERLAY}.
     */
    public BookModel withLeftFrameOverride(BookFrameOverlay overlay) {
        this.leftFrameOverlay = overlay;
        return this;
    }

    /**
     * Sets the image to use as an overlay for the right frame.
     * This can either be just a copy of the same portion of the frame texture, or a different texture to show some unique details that are not repeated.
     * Default is {@link Data.Book#DEFAULT_RIGHT_FRAME_OVERLAY}.
     */
    public BookModel withRightFrameOverride(BookFrameOverlay overlay) {
        this.rightFrameOverlay = overlay;
        return this;
    }

    public BookModel withBookContentTexture(ResourceLocation bookContentTexture) {
        this.bookContentTexture = bookContentTexture;
        return this;
    }

    public BookModel withCraftingTexture(ResourceLocation craftingTexture) {
        this.craftingTexture = craftingTexture;
        return this;
    }

    /**
     * Sets the sound to play when turning a page in the book.
     * Default is {@link Data.Book#DEFAULT_PAGE_TURN_SOUND}.
     */
    public BookModel withTurnPageSound(ResourceLocation turnPageSound) {
        this.turnPageSound = turnPageSound;
        return this;
    }

    public BookModel withModel(ResourceLocation model) {
        this.model = model;
        return this;
    }

    /**
     * Sets the display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style)
     * If in index mode, the frame textures will be ignored, instead bookContentTexture will be used
     */
    public BookModel withDisplayMode(BookDisplayMode displayMode) {
        this.displayMode = displayMode;
        return this;
    }

    public BookModel withGenerateBookItem(boolean generateBookItem) {
        this.generateBookItem = generateBookItem;
        return this;
    }

    public BookModel withCustomBookItem(ResourceLocation customBookItem) {
        this.customBookItem = customBookItem;
        return this;
    }

    public BookModel withDefaultTitleColor(int defaultTitleColor) {
        this.defaultTitleColor = defaultTitleColor;
        return this;
    }

    public BookModel withCategoryButtonIconScale(float categoryButtonIconScale) {
        this.categoryButtonIconScale = categoryButtonIconScale;
        return this;
    }

    public BookModel withCategories(List<BookCategoryModel> categories) {
        categories.forEach(category -> category.book = this);
        this.categories.addAll(categories);
        return this;
    }

    public BookModel withCategories(BookCategoryModel... categories) {
        return this.withCategories(List.of(categories));
    }

    public BookModel withCategory(BookCategoryModel category) {
        category.book = this;
        this.categories.add(category);
        return this;
    }

    public BookModel withCommands(List<BookCommandModel> commands) {
        commands.forEach(command -> command.book = this);
        this.commands.addAll(commands);
        return this;
    }

    public BookModel withCommands(BookCommandModel... commands) {
        return this.withCommands(List.of(commands));
    }

    public BookModel withCommand(BookCommandModel command) {
        command.book = this;
        this.commands.add(command);
        return this;
    }

    public BookModel withAutoAddReadConditions(boolean autoAddReadConditions) {
        this.autoAddReadConditions = autoAddReadConditions;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the x position (basically, create a left margin).
     * Will be automatically subtracted from the width to avoid overflow.
     */
    public BookModel withBookTextOffsetX(int bookTextOffsetX) {
        this.bookTextOffsetX = bookTextOffsetX;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the y position (basically, create a top margin).
     */
    public BookModel withBookTextOffsetY(int bookTextOffsetY) {
        this.bookTextOffsetY = bookTextOffsetY;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the width (allows to create a right margin)
     * To make the line end move to the left (as it would for a margin setting in eg css), use a negative value.
     */
    public BookModel withBookTextOffsetWidth(int bookTextOffsetWidth) {
        this.bookTextOffsetWidth = bookTextOffsetWidth;
        return this;
    }

    /**
     * When rendering book text holders, add this offset to the height (allows to create a bottom margin)
     * To make the bottom end of the text move up (as it would for a margin setting in eg css), use a negative value.
     */
    public BookModel withBookTextOffsetHeight(int bookTextOffsetHeight) {
        this.bookTextOffsetHeight = bookTextOffsetHeight;
        return this;
    }

    public BookModel withCategoryButtonXOffset(int categoryButtonXOffset) {
        this.categoryButtonXOffset = categoryButtonXOffset;
        return this;
    }

    public BookModel withCategoryButtonYOffset(int categoryButtonYOffset) {
        this.categoryButtonYOffset = categoryButtonYOffset;
        return this;
    }

    public BookModel withSearchButtonXOffset(int searchButtonXOffset) {
        this.searchButtonXOffset = searchButtonXOffset;
        return this;
    }

    public BookModel withSearchButtonYOffset(int searchButtonYOffset) {
        this.searchButtonYOffset = searchButtonYOffset;
        return this;
    }

    public BookModel withReadAllButtonYOffset(int readAllButtonYOffset) {
        this.readAllButtonYOffset = readAllButtonYOffset;
        return this;
    }

    /**
     * If this entry is set the book will ignore all other content and just display this entry.
     * Note that the entry still needs to be in a valid category, even if the category is not displayed.
     *
     * @param leafletEntry The ResourceLocation of the entry to display
     */
    public BookModel withLeafletEntry(ResourceLocation leafletEntry) {
        this.leafletEntry = leafletEntry;
        return this;
    }

    public BookModel withPageDisplayMode(PageDisplayMode pageDisplayMode) {
        this.pageDisplayMode = pageDisplayMode;
        return this;
    }

    public BookModel withSinglePageTexture(ResourceLocation singlePageTexture) {
        this.singlePageTexture = singlePageTexture;
        return this;
    }

    /**
     * If true, invalid links do not show an error screen when opening the book.
     * Instead, the book and pages will open, but the link will not work.
     *
     * The main use for this is for mods that have external translators for their books, where translations might be outdated after entries have been removed or moved.
     */
    public BookModel withAllowOpenBooksWithInvalidLinks(boolean value) {
        this.allowOpenBooksWithInvalidLinks = value;
        return this;
    }

    /**
     * If true, this model will not generate a book.json file, but the categories and entries will still be generated.
     */
    public BookModel withDontGenerateJson(boolean value) {
        this.dontGenerateJson = value;
        return this;
    }
}
