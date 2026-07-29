/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.registry.ItemRegistry;

/**
 * An opinionated book leaflet sub provider with helper methods to generate a leaflet more easily.
 */
public abstract class LeafletSubProvider extends SingleBookSubProvider {

    /**
     * Creates a leaflet subprovider.
     * <p>
     * Language access is provided via setup injection at generate time.
     *
     * @param bookId the book/leaflet id
     * @param modId  the mod id
     */
    public LeafletSubProvider(String bookId, String modId) {
        super(bookId, modId);
    }

    @Override
    protected void generateCategories() {
        var leafletCategoryProvider = new LeafletCategoryProvider(this);
        var leafletCategory = this.add(leafletCategoryProvider.generate());
        var entryProvider = this.createEntryProvider(leafletCategoryProvider);
        leafletCategory.withEntry(entryProvider.generate());
    }

    @Override
    protected final BookModel additionalSetup(BookModel book) {
        return this.additionalLeafletSetup(
                book.withLeafletEntry(this.modLoc(LeafletCategoryProvider.ID, LeafletEntryProvider.ID))
                        .withModel(ItemRegistry.LEAFLET.getId())
        );
    }

    /**
     * Implement this and modify the book as needed for additional config.
     * Context already is set to this book.
     */
    protected BookModel additionalLeafletSetup(BookModel book) {
        return book;
    }

    protected abstract LeafletEntryProvider createEntryProvider(CategoryProvider parent);
}
