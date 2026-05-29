/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Book;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.book.PageDisplayMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookModel {

    /**
     * The book ID, e.g. "modonomicon:demo". The ID must be unique (usually that is guaranteed by the mod ID).
     */
    protected Identifier id;
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
     * If in index mode, the themed double-page background is used instead of the node frame texture.
     */
    protected BookDisplayMode displayMode = BookDisplayMode.NODE;

    /**
     * The creative tab to add the book to.
     */
    protected Identifier creativeTab = Identifier.parse("modonomicon:modonomicon");

    /**
     * If true, automatically generates an item for this book and registers it with the creative tab.
     */
    protected boolean generateBookItem = true;

    /**
     * The item model to use for the book. Only used if generateBookItem = true.
     */
    protected Identifier model = Identifier.parse(Book.DEFAULT_MODEL);

    /**
     * If set, uses this item for the book. That means you need to implement all functionality to open the book yourself.
     */
    @Nullable
    protected Identifier customBookItem = null;

    /**
     * The font to use for the book text.
     */
    protected Identifier font = Identifier.parse(Book.DEFAULT_FONT);

    protected PageDisplayMode pageDisplayMode = PageDisplayMode.DOUBLE_PAGE;
    protected Identifier turnPageSound = Identifier.parse(Book.DEFAULT_PAGE_TURN_SOUND);
    protected BookThemeModel theme;

    protected List<BookCategoryModel> categories = new ArrayList<>();
    protected List<BookCommandModel> commands = new ArrayList<>();

    /**
     * If this entry is set the book will ignore all other content and just display this entry.
     * Note that the entry still needs to be in a valid category, even if the category is not displayed.
     *
     * The book will be treated as a book in index mode (that means, no big "node view" background will be shown behind the entry).
     */
    @Nullable
    protected Identifier leafletEntry;

    /**
     * If true, invalid links do not show an error screen when opening the book.
     * Instead, the book and pages will open, but the link will not work.
     */
    protected boolean allowOpenBooksWithInvalidLinks = false;

    /**
     * If true, a "Recently Unlocked" button is shown that lists entries sorted by unlock timestamp.
     * Useful for books with many conditional entries. Defaults to true.
     */
    protected boolean showRecentlyUnlocked = true;

    /**
     * If true, this model will not generate a book.json file, but the categories and entries will still be generated.
     */
    protected boolean dontGenerateJson = false;

    protected BookModel(Identifier id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id   The book ID, e.g. "modonomicon:demo". The ID must be unique (usually that is guaranteed by the mod ID).
     * @param name Should be a translation key.
     */
    public static BookModel create(Identifier id, String name) {
        return new BookModel(id, name);
    }

    public Identifier getTurnPageSound() {
        return this.turnPageSound;
    }

    public boolean generateBookItem() {
        return this.generateBookItem;
    }

    @Nullable
    public Identifier getCustomBookItem() {
        return this.customBookItem;
    }

    public List<BookCategoryModel> getCategories() {
        return this.categories;
    }

    public List<BookCommandModel> getCommands() {
        return this.commands;
    }

    public Identifier getId() {
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

    public Identifier getCreativeTab() {
        return this.creativeTab;
    }

    public Identifier getModel() {
        return this.model;
    }

    public Identifier getFont() {
        return this.font;
    }

    public BookDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public PageDisplayMode getPageDisplayMode() {
        return this.pageDisplayMode;
    }

    public BookThemeModel getTheme() {
        return this.theme;
    }

    public @Nullable Identifier getLeafletEntry() {
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
        json.addProperty("font", this.font.toString());
        json.addProperty("turn_page_sound", this.turnPageSound.toString());

        json.addProperty("generate_book_item", this.generateBookItem);
        if (this.customBookItem != null) {
            json.addProperty("custom_book_item", this.customBookItem.toString());
        }

        if (this.leafletEntry != null) {
            json.addProperty("leaflet_entry", this.leafletEntry.toString());
        }

        json.addProperty("page_display_mode", this.pageDisplayMode.getSerializedName());

        json.addProperty("allow_open_book_with_invalid_links", this.allowOpenBooksWithInvalidLinks);

        json.addProperty("show_recently_unlocked", this.showRecentlyUnlocked);

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

    public BookModel withCreativeTab(Identifier creativeTab) {
        this.creativeTab = creativeTab;
        return this;
    }

    public BookModel withFont(Identifier font) {
        this.font = font;
        return this;
    }

    /**
     * Sets the sound to play when turning a page in the book.
     * Default is {@link Data.Book#DEFAULT_PAGE_TURN_SOUND}.
     */
    public BookModel withTurnPageSound(Identifier turnPageSound) {
        this.turnPageSound = turnPageSound;
        return this;
    }

    public BookModel withModel(Identifier model) {
        this.model = model;
        return this;
    }

    /**
     * Sets the display mode - node based (thaumonomicon style) or index based (lexica botania / patchouli style).
     * If in index mode, the themed double-page background is used instead of the node frame texture.
     */
    public BookModel withDisplayMode(BookDisplayMode displayMode) {
        this.displayMode = displayMode;
        return this;
    }

    public BookModel withGenerateBookItem(boolean generateBookItem) {
        this.generateBookItem = generateBookItem;
        return this;
    }

    public BookModel withCustomBookItem(Identifier customBookItem) {
        this.customBookItem = customBookItem;
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

    public BookModel withTheme(Consumer<BookThemeModel> consumer) {
        consumer.accept(this.theme());
        return this;
    }

    protected BookThemeModel theme() {
        if (this.theme == null) {
            this.theme = new BookThemeModel();
        }
        return this.theme;
    }

    /**
     * If this entry is set the book will ignore all other content and just display this entry.
     * Note that the entry still needs to be in a valid category, even if the category is not displayed.
     *
     * @param leafletEntry The Identifier of the entry to display
     */
    public BookModel withLeafletEntry(Identifier leafletEntry) {
        this.leafletEntry = leafletEntry;
        return this;
    }

    public BookModel withPageDisplayMode(PageDisplayMode pageDisplayMode) {
        this.pageDisplayMode = pageDisplayMode;
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
     * If true, a "Recently Unlocked" button is shown that lists entries sorted by unlock timestamp.
     * Useful for books with many conditional entries. Defaults to true.
     */
    public BookModel withShowRecentlyUnlocked(boolean value) {
        this.showRecentlyUnlocked = value;
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
