/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CategoryProvider extends ModonomiconProviderBase {

    protected final ModonomiconProviderBase parent;
    protected final CategoryEntryMap entryMap;
    protected final Map<String, List<BookPageModel>> cachedPages = new Object2ObjectOpenHashMap<>();
    protected BookCategoryModel category;
    protected int currentSortIndex;

    public CategoryProvider(ModonomiconProviderBase parent) {
        super(parent.modId(), parent.lang(), parent.langs(), parent.context(), parent.condition());
        this.parent = parent;
        this.entryMap = new CategoryEntryMap();
        this.category = null;
        this.currentSortIndex = 0;
    }

    protected CategoryEntryMap entryMap() {
        return this.entryMap;
    }

    @Override
    protected Map<String, String> macros() {
        return Stream.concat(super.macros().entrySet().stream(), this.parent.macros().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(String location, ResourceLocation texture) {
        return this.entry(location).withIcon(texture);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(String location, ResourceLocation texture, int width, int height) {
        return this.entry(location).withIcon(texture, width, height);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(String location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(char location, ResourceLocation texture) {
        return this.entry(location).withIcon(texture);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(char location, ResourceLocation texture, int width, int height) {
        return this.entry(location).withIcon(texture, width, height);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(char location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(char location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry(String location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }

    /**
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected BookEntryModel entry() {
        var entry = BookEntryModel.create(
                        this.modLoc(this.context().categoryId() + "/" + this.context().entryId()),
                        this.context().entryName()
                )
                .withDescription(this.context().entryDescription());
        if (this.cachedPages.containsKey(this.context().entry())) {
            entry.withPages(this.cachedPages.get(this.context().entry()));
            this.cachedPages.remove(this.context().entry());
        }
        return entry;
    }

    /**
     * Adds a page to the cached pages of this category provider.
     * Make sure to call this.context().page(<pageId>) before calling this method!
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param model the page model
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected <T extends BookPageModel> T page(T model) {
        this.cachedPages.computeIfAbsent(this.context().entry(), k -> new ArrayList<>()).add(model);
        return model;
    }

    /**
     * Registers the page with the current context and adds it to the cached pages of this category provider.
     * No need to call this.context().page(<pageId>). This method will do that for you.
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param modelSupplier A supplier that provides a page model. It is a supplier, because that way you can use this.context() within the supplier and it will correctly use the given page as part of the context.
     * @deprecated use {@link EntryProvider()} instead.
     */
    @Deprecated(forRemoval = true)
    protected <T extends BookPageModel> T page(String page, Supplier<T> modelSupplier) {
        this.context().page(page);
        var model = modelSupplier.get();
        this.cachedPages.computeIfAbsent(this.context().entry(), k -> new ArrayList<>()).add(model);
        return model;
    }

    protected BookEntryParentModel parent(BookEntryModel parentEntry) {
        return BookEntryParentModel.create(parentEntry.getId());
    }

    protected BookEntryModel add(BookEntryModel entry) {
        if (entry.getSortNumber() == -1) {
            entry.withSortNumber(this.currentSortIndex++);
        }
        this.category.withEntry(entry);
        return entry;
    }

    protected List<BookEntryModel> add(List<BookEntryModel> entries) {
        for (var entry : entries) {
            if (entry.getSortNumber() == -1) {
                entry.withSortNumber(this.currentSortIndex++);
            }
        }
        this.category.withEntries(entries);
        return entries;
    }

    /**
     * Call this in your BookProvider to get the category.
     */
    public BookCategoryModel generate() {
        this.context().category(this.categoryId());

        var map = this.generateEntryMap();
        if(map != null && map.length > 0)
            this.entryMap().setMap(this.generateEntryMap());

        var category = BookCategoryModel.create(
                        this.modLoc(this.context().categoryId()),
                        this.context().categoryName()
                );

        this.add(this.context().categoryName(), this.categoryName());
        var categoryDescription = this.categoryDescription();
        if (!StringUtil.isNullOrEmpty(categoryDescription)) {
            this.add(this.context().categoryDescription(), categoryDescription);
            category.withDescription(this.context().categoryDescription());
        }

        category.withIcon(this.categoryIcon());

        this.category = this.additionalSetup(category);
        this.generateEntries();
        return this.category;
    }

    /**
     * Implement this and return your entry map String to be used in the CategoryEntryMap
     */
    protected abstract String[] generateEntryMap();

    /**
     * Implement this and in it generate, link (= set parents and conditions) and .add() your entries.
     * Context already is set to this category.
     */
    protected abstract void generateEntries();

    /**
     * Implement this and modify the category as needed for additional config.
     * Entries should not be added here, instead call .add() in generateEntries().
     * Context already is set to this category.
     */
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category;
    }

    /**
     * Implement this and return the category name in the main language.
     */
    protected abstract String categoryName();

    /**
     * Implement this and return the category description in the main language.
     */
    protected String categoryDescription() {
        return "";
    }

    /**
     * Implement this and return the desired icon for the category.
     */
    protected abstract BookIconModel categoryIcon();

    /**
     * Implement this and return the desired id for the category.
     */

    protected abstract String categoryId();
}