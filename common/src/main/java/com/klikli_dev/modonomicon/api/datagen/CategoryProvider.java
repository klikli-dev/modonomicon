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
import net.minecraft.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CategoryProvider extends CategoryProviderBase {

    protected CategoryEntryMap entryMap;
    protected BookCategoryModel category;
    protected int currentSortIndex;

    public CategoryProvider(ModonomiconProviderBase parent) {
        super(parent, parent.modId(), parent.lang(), parent.langsAsMapOfBiConsumers(), parent.context(), parent.condition());
        this.entryMap = new CategoryEntryMap();
        this.category = null;
        this.currentSortIndex = 0;
    }

    public CategoryEntryMap entryMap() {
        return this.entryMap;
    }

    @Override
    protected Map<String, String> macros() {
        return Stream.concat(super.macros().entrySet().stream(), this.parent.macros().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
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
        this.context().category(this.categoryId());

        var map = this.generateEntryMap();
        if (map != null && map.length > 0)
            this.entryMap().setMap(map);

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
    @Override
    public abstract String categoryId();
}