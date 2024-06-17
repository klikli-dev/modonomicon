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
import net.minecraft.world.phys.Vec2;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntryProvider extends ModonomiconProviderBase {

    protected final CategoryProviderBase parent;

    protected BookEntryModel entry;

    public EntryProvider(CategoryProviderBase parent) {
        super(parent.modId(), parent.lang(), parent.langs(), parent.context(), parent.condition());
        this.parent = parent;
        this.entry = null;
    }

    @Override
    protected Map<String, String> macros() {
        return Stream.concat(super.macros().entrySet().stream(), this.parent.macros().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
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
    protected void pageText(String text, Object... args) {
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
    protected <T extends BookPageModel<?>> T page(T model) {
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
    protected <T extends BookPageModel<?>> T page(String page, Supplier<T> modelSupplier) {
        this.context().page(page);
        var model = modelSupplier.get();
        return this.add(model);
    }

    protected <T extends BookPageModel<?>> T add(T page) {
        this.entry.withPage(page);
        return page;
    }

    protected List<BookPageModel<?>> add(List<BookPageModel<?>> pages) {
        this.entry.withPages(pages);
        return pages;
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     * <p>
     * This overload should only be used in index mode, where no location is needed.
     */
    public BookEntryModel generate() {
        return this.generate(Vec2.ZERO);
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(String location) {
        if (location.length() == 1)
            return this.generate(location.charAt(0));
        return this.generate(this.parent.entryMap().get(location));
    }


    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(char location) {
        return this.generate(this.parent.entryMap().get(location));
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(Vec2 location) {
        this.context().entry(this.entryId());

        var entry = BookEntryModel.create(
                        this.modLoc(this.context().categoryId() + "/" + this.context().entryId()),
                        this.context().entryName()
                )
                .withDescription(this.context().entryDescription());

        this.add(this.context().entryName(), this.entryName());
        this.add(this.context().entryDescription(), this.entryDescription());
        entry.withIcon(this.entryIcon());
        entry.withLocation(location);
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