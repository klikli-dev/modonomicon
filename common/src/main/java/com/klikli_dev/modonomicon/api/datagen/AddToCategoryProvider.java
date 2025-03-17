/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;

/**
 * An opinionated category provider with helper methods to generate contents for an existing category in an existing book more easily. E.g. for use in a mod that adds to a book from another mod, or modpacks that use datapacks to add to a book.
 * <p>
 * Generates book entries, but no category.json
 */
public abstract class AddToCategoryProvider extends CategoryProvider {

    public AddToCategoryProvider(ModonomiconProviderBase parent) {
        super(parent);
    }

    /**
     * Call this in your BookProvider to get the category.
     */
    public BookCategoryModel generate() {
        this.context().category(this.categoryId());

        var map = this.generateEntryMap();
        if (map != null && map.length > 0)
            this.entryMap().setMap(map);

        this.category = BookCategoryModel.create(
                this.modLoc(this.context().categoryId()),
                this.context().categoryName()
        );
        this.category.withDontGenerateJson(true);

        this.generateEntries();
        return this.category;
    }

    /**
     * Additional setup is not possible for an AddTo provider, as no category json is generated, only entries.
     */
    @Override
    protected final BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category);
    }

    /**
     * Category name cannot be set for an AddTo provider, as no category json is generated, only entries.
     */
    @Override
    protected final String categoryName() {
        return "";
    }

    /**
     * Category name cannot be set for an AddTo provider, as no category json is generated, only entries.
     */
    @Override
    protected final String categoryDescription() {
        return super.categoryDescription();
    }

    /**
     * Category name cannot be set for an AddTo provider, as no category json is generated, only entries.
     */
    @Override
    protected final BookIconModel categoryIcon() {
        return null;
    }

    /**
     * Category ID always must be target category id.
     */
    @Override
    public final String categoryId() {
        return this.targetCategoryId();
    }

    /**
     * Implement this and return the id of the category you want to add to.
     */
    public abstract String targetCategoryId();
}