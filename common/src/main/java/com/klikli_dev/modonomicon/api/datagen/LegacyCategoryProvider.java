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

/**
 * A category provider that is oriented on the legacy API for easier migration.
 */
public abstract class LegacyCategoryProvider extends CategoryProviderBase {

    protected final Map<String, List<BookPageModel<?>>> cachedPages = new Object2ObjectOpenHashMap<>();
    protected CategoryEntryMap entryMap;
    protected BookCategoryModel category;
    protected int currentSortIndex;
    protected String categoryId;

    public LegacyCategoryProvider(ModonomiconProviderBase parent, String categoryId) {
        super(parent, parent.modId(), parent.lang(), parent.langs(), parent.context(), parent.condition());
        this.entryMap = new CategoryEntryMap();
        this.category = null;
        this.currentSortIndex = 0;
        this.categoryId = categoryId;
    }

    public String categoryId() {
        return this.categoryId;
    }

    public CategoryEntryMap entryMap() {
        return this.entryMap;
    }

    @Override
    protected Map<String, String> macros() {
        return Stream.concat(super.macros().entrySet().stream(), this.parent.macros().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    protected BookEntryModel entry(String location, ResourceLocation texture) {
        return this.entry(location).withIcon(texture);
    }

    protected BookEntryModel entry(String location, ResourceLocation texture, int width, int height) {
        return this.entry(location).withIcon(texture, width, height);
    }

    protected BookEntryModel entry(String location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(char location, ResourceLocation texture) {
        return this.entry(location).withIcon(texture);
    }

    protected BookEntryModel entry(char location, ResourceLocation texture, int width, int height) {
        return this.entry(location).withIcon(texture, width, height);
    }

    protected BookEntryModel entry(char location, ItemLike icon) {
        return this.entry(location).withIcon(icon);
    }

    protected BookEntryModel entry(char location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }


    protected BookEntryModel entry(String location) {
        return this.entry().withLocation(this.entryMap().get(location));
    }

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

    protected <T extends BookPageModel<?>> T page(T model) {
        this.cachedPages.computeIfAbsent(this.context().entry(), k -> new ArrayList<>()).add(model);
        return model;
    }

    protected <T extends BookPageModel<?>> T page(String page, Supplier<T> modelSupplier) {
        this.context().page(page);
        var model = modelSupplier.get();
        this.cachedPages.computeIfAbsent(this.context().entry(), k -> new ArrayList<>()).add(model);
        return model;
    }

    protected BookEntryParentModel parent(BookEntryModel parentEntry) {
        return BookEntryParentModel.create(parentEntry.getId());
    }

    public BookEntryModel add(BookEntryModel entry) {
        if (entry.getSortNumber() == -1) {
            entry.withSortNumber(this.currentSortIndex++);
        }
        this.category.withEntry(entry);
        return entry;
    }

    public List<BookEntryModel> add(List<BookEntryModel> entries) {
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
        this.context().category(this.categoryId);
        this.entryMap().setMap(this.generateEntryMap());
        this.category = this.generateCategory();
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
     * Implement this and return your category.
     * Entries should not be added here, instead call .add() in generateEntries().
     * Context already is set to this category.
     */
    protected abstract BookCategoryModel generateCategory();
}