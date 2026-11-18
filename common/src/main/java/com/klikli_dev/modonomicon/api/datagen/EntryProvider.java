/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.phys.Vec2;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntryProvider extends ModonomiconProviderBase {

    protected final CategoryProviderBase parent;

    protected BookEntryModel entry;

    public EntryProvider(CategoryProviderBase parent) {
        super(parent.modId(), parent.lang(), parent.langsAsMapOfBiConsumers(), parent.context(), parent.condition());
        this.parent = parent;
        this.entry = null;
    }

    @Override
    protected HolderLookup.Provider registries() {
        return this.parent.registries();
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
        model.withId(page);
        return this.add(model);
    }

    /**
     * Controls whether pages for this entry are generated as separate JSON files.
     * Default is {@code true} (separate files).
     * Set to {@code false} to include pages inline in the entry's JSON (legacy behavior).
     */
    protected void generatePagesAsFiles(boolean generatePagesAsFiles) {
        this.entry.withGeneratePagesAsFiles(generatePagesAsFiles);
    }

    /**
     * Controls whether tabs for this entry are generated as separate JSON files.
     * Default is {@code true} (separate files).
     * Set to {@code false} to include tabs inline in the entry's JSON.
     */
    protected void generateTabsAsFiles(boolean generateTabsAsFiles) {
        this.entry.withGenerateTabsAsFiles(generateTabsAsFiles);
    }

    /**
     * Adds a tab title for the current context to the underlying language provider.
     * Like {@link #pageTitle(String)} it can be called right after {@link #tab(String, Supplier)}, as the tab stays the current context
     * until the next tab is declared. Use the translation key {@link #tabTitleKey()} in the tab model, e.g. {@code .withTitle(this.tabTitleKey())}.
     */
    protected void tabTitle(String title) {
        this.add(this.context().tabTitle(), title);
    }

    /**
     * The translation key of the title of the tab in the current context.
     */
    protected String tabTitleKey() {
        return this.context().tabTitle();
    }

    /**
     * Adds a tab text for the current context to the underlying language provider, e.g. for the text of the tab page.
     * Like {@link #pageText(String)} it can be called right after {@link #tab(String, Supplier)}.
     * Use the translation key {@link #tabTextKey()} in the tab model.
     */
    protected void tabText(String text) {
        this.add(this.context().tabText(), text);
    }

    /**
     * The translation key of the text of the tab in the current context.
     */
    protected String tabTextKey() {
        return this.context().tabText();
    }

    /**
     * Registers a tab with the current context and adds it to the tabs of the entry created with this.entry(...).
     * The tab is pinned to the page most recently declared with {@link #page(String, Supplier)} in this entry,
     * i.e. it is only visible while that page is on the open spread. Several tabs declared after the same page all pin to it.
     * An anchor set explicitly on the returned model is kept.
     * The tab is written as a file named after the id, see {@link #generateTabsAsFiles(boolean)}.
     * Needs to be called after this.context().entry(<entryId>) and after the page it should pin to has been declared.
     *
     * @param id            the tab id, used as file name and as id of the tab page if it has none
     * @param modelSupplier A supplier that provides a tab model. It is a supplier, because that way you can use this.context() within the supplier, e.g. for tabTitleKey().
     * @throws IllegalStateException if no page has been declared yet in this entry, use {@link #entryTab(String, Supplier)} for entry-wide tabs
     */
    protected BookEntryTabModel tab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        var pageId = this.context().pageId();
        if (pageId == null) {
            throw new IllegalStateException("Tab '" + id + "' in entry '" + this.entryId() + "' has no page to pin to. "
                    + "Declare a page with this.page(...) before this.tab(...), or use this.entryTab(...) for a tab that is visible for the whole entry.");
        }

        var model = this.addTab(id, modelSupplier);
        if (model.getAnchor() == null) {
            model.withAnchor(pageId);
        }
        return model;
    }

    /**
     * Registers an entry-wide tab with the current context and adds it to the tabs of the entry created with this.entry(...).
     * Unlike {@link #tab(String, Supplier)} the tab is not pinned to a page, so it is visible for the whole entry.
     * An anchor set explicitly on the returned model is kept.
     * The tab is written as a file named after the id, see {@link #generateTabsAsFiles(boolean)}.
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param id            the tab id, used as file name and as id of the tab page if it has none
     * @param modelSupplier A supplier that provides a tab model. It is a supplier, because that way you can use this.context() within the supplier, e.g. for tabTitleKey().
     */
    protected BookEntryTabModel entryTab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        return this.addTab(id, modelSupplier);
    }

    private BookEntryTabModel addTab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        this.context().tab(id);
        var model = modelSupplier.get();
        model.withId(id);
        this.entry.withTab(model);
        return model;
    }

    /**
     * Ensures present tab ids are unique within the entry and that every tab anchor refers to a page of the entry.
     */
    private void validateTabs() {
        var pageIds = this.entry.getPages().stream()
                .map(BookPageModel::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var tabIds = new HashSet<String>();
        for (var tab : this.entry.getTabs()) {
            //inline tabs may omit the id, tabs written as files are checked for an id in BookProvider
            var tabId = tab.getId();
            if (tabId != null && !tabId.isEmpty() && !tabIds.add(tabId)) {
                throw new IllegalStateException("Duplicate tab id '" + tab.getId() + "' in entry '" + this.entryId() + "'.");
            }

            if (tab.getAnchor() != null && !pageIds.contains(tab.getAnchor())) {
                throw new IllegalStateException("Tab '" + tab.getId() + "' in entry '" + this.entryId() + "' is anchored to page '"
                        + tab.getAnchor() + "', but the entry has no page with that id.");
            }
        }
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
     * This overload resolves a location from {@link #entryId()} via the parent category's
     * {@code layout()} helper.
     * <p>
     * If no explicit layout position exists yet, the entry starts at {@link Vec2#ZERO}, which allows later
     * post-generation layout mutation via {@code this.layout().entry(model)...}.
     */
    public BookEntryModel generate() {
        return this.generate(this.parent.layout().getOrDefault(this.entryId(), Vec2.ZERO));
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
        this.validateTabs();

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
    protected String entryDescription() {
        return "";
    }

    /**
     * Implement this and return the GUI texture used for the entry background.
     */
    protected abstract GuiSprite entryBackground();

    /**
     * Implement this and return the desired icon for the entry.
     */
    protected abstract BookIconModel entryIcon();

    /**
     * Implement this and return the desired id for the entry.
     */

    protected abstract String entryId();

}

