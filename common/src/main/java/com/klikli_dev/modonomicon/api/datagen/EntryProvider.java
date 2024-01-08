/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Supplier;

public abstract class EntryProvider {

    protected CategoryProvider parent;

    protected BookEntryModel entry;

    public EntryProvider(CategoryProvider parent) {
        this.parent = parent;
        this.entry = null;
    }

    protected ModonomiconLanguageProvider lang() {
        return this.parent.lang();
    }

    protected ModonomiconLanguageProvider lang(String locale) {
        return this.parent.lang();
    }

    protected ResourceLocation modLoc(String name) {
        return this.parent.modLoc(name);
    }

    protected BookContextHelper context() {
        return this.parent.context();
    }

    /**
     * Apply all macros of the category provider and its book provider to the input string.
     */
    protected String macro(String input) {
        return this.parent.macro(input);
    }

    /**
     * Format a string with the given arguments using MessageFormat.format()
     */
    protected String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    /**
     * Create a link to an entry in the same book.
     */
    protected String entryLink(String text, String category, String entry) {
        return this.parent.entryLink(text, category, entry);
    }

    /**
     * Create a link to a category in the same book.
     */
    protected String categoryLink(String text, String category) {
        return this.parent.categoryLink(text, category);
    }

    /**
     * Create an item link with no text (will use item name)
     */
    protected String itemLink(ItemLike item) {
        return this.parent.itemLink(item);
    }

    /**
     * Create an item link with a custom text (instead of item name)
     */
    protected String itemLink(String text, ItemLike item) {
        return this.parent.itemLink(text, item);
    }

    /**
     * Create a command link for the command with the given id.
     */
    protected String commandLink(String text, String commandId) {
        return this.parent.commandLink(text, commandId);
    }

    /**
     * Dummy entry link for use in the book provider, as the linked entry is not available at that point.
     * Replace with identical call to entryLink once the entry is available.
     */
    protected String entryLinkDummy(String text, String category, String entry) {
        return this.parent.entryLinkDummy(text, category, entry);
    }

    /**
     * Dummy category link for use in the book provider, as the linked category is not available at that point.
     * Replace with identical call to categoryLink once the entry is available.
     */
    protected String categoryLinkDummy(String text, String category) {
        return this.parent.categoryLinkDummy(text, category);
    }

    /**
     * Adds a page title for the current context to the underlying language provider.
     */
    protected void pageTitle(String title) {
        this.add(this.context().pageTitle(), title);
    }

    /**
     * Adds a page text for the current context to the underlying language provider.
     */
    protected void pageText(String text) {
        this.add(this.context().pageText(), text);
    }

    /**
     * Adds a page text for the current context to the underlying language provider.
     */
    protected void pageText(String text, Object... args){
        this.add(this.context().pageText(), text, args);
    }

    /**
     * Adds a page to the cached pages of this category provider.
     * Make sure to call this.context().page(<pageId>) before calling this method!
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param model the page model
     */
    protected <T extends BookPageModel> T page(T model) {
        return this.add(model);
    }

    /**
     * Registers the page with the current context and adds it to the cached pages of this category provider.
     * No need to call this.context().page(<pageId>). This method will do that for you.
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param modelSupplier A supplier that provides a page model. It is a supplier, because that way you can use this.context() within the supplier and it will correctly use the given page as part of the context.
     */
    protected <T extends BookPageModel> T page(String page, Supplier<T> modelSupplier) {
        this.context().page(page);
        var model = modelSupplier.get();
        return this.add(model);
    }

    /**
     * Add translation to the default translation provider.
     * This will apply all macros registered in this category provider and its parent book provider.
     */
    protected void add(String key, String value) {
        this.lang().add(key, this.macro(value));
    }

    /**
     * Add translation to the given translation provider.
     * This will apply all macros registered in this category provider and its parent book provider.
     * <p>
     * Sample usage: this.add(this.lang("ru_ru"), "category", "Text");
     */
    protected void add(AbstractModonomiconLanguageProvider translation, String key, String value) {
        translation.add(key, this.macro(value));
    }

    /**
     * Adds translation to the default translation provider with a pattern and arguments, internally using MessageFormat to format the pattern.
     * This will apply all macros registered in this category provider and its parent book provider.
     */
    protected void add(String key, String pattern, Object... args) {
        this.add(key, this.format(pattern, args));
    }

    /**
     * Adds translation to the given translation provider with a pattern and arguments, internally using MessageFormat to format the pattern.
     * This will apply all macros registered in this category provider and its parent book provider.
     * <p>
     * Sample usage: this.add(this.lang("ru_ru"), "category", "pattern", "arg1");
     */
    protected void add(AbstractModonomiconLanguageProvider translation, String key, String pattern, Object... args) {
        this.add(translation, key, this.format(pattern, args));
    }

    protected <T extends BookPageModel> T add(T page) {
        this.entry.withPage(page);
        return page;
    }

    protected <T extends BookPageModel> List<T> add(List<T> pages) {
        //noinspection unchecked
        this.entry.withPages((List<BookPageModel>) pages);
        return pages;
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(char location) {
        this.context().entry(this.entryId());

        var entry = BookEntryModel.create(
                        this.modLoc(this.context().categoryId() + "/" + this.context().entryId()),
                        this.context().entryName()
                )
                .withDescription(this.context().entryDescription());

        this.add(this.context().entryName(), this.entryName());
        this.add(this.context().entryDescription(), this.entryDescription());
        entry.withIcon(this.entryIcon());
        entry.withLocation(this.parent.entryMap().get(location));
        entry.withEntryBackground(this.entryBackground());

        this.entry = this.additionalSetup(entry);

        this.generatePages();

        this.parent.add(this.entry);
        return this.entry;
    }

    /**
     * Implement this and in it generate, and .add() (or .page()) your pages.
     * Context already is set to this entry.
     */
    protected abstract void generatePages();

    /**
     * Implement this and modify the entry as needed for additional config.
     * Pages should not be added here, instead call .add() in generatePages().
     * Context already is set to this entry.
     */
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return entry;
    }

    /**
     * Implement this and return the entry name in the main language.
     */
    protected abstract String entryName();

    /**
     * Implement this and return the entry description in the main language.
     */
    protected abstract String entryDescription();

    /**
     * Implement this and return the U/V coordinates of the entry background. See also @link{BookEntryModel#withEntryBackground(int, int)}
     */
    protected abstract Pair<Integer, Integer> entryBackground();

    /**
     * Implement this and return the desired icon for the entry.
     */
    protected abstract BookIconModel entryIcon();

    /**
     * Implement this and return the desired id for the entry.
     */

    protected abstract String entryId();

}