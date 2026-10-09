// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.IndexMarkerCraftedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.IndexMarkerExplicitEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.IndexMarkerIntroEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class IndexMarkersCategory extends CategoryProvider {
    public static final String ID = "index_markers";

    public IndexMarkersCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        //No parents here: index mode lists have no node graph, and parentless entries
        //are skipped by the automatic hierarchy research, so these stay unlocked
        //and the marker dot - not an unlock gate - is what is on display.
        this.add(new IndexMarkerIntroEntry(this).generate());

        //explicit marker condition pointing at a manually authored research node.
        //research nodes are shared across books, so this reuses the demo book research.
        this.add(new IndexMarkerExplicitEntry(this).generate()
                .withMarker(DemoResearch.CRAFTING_STICK));

        //auto-generated marker research into this book's own generated bundle:
        //dot clears once a diamond sword has been crafted.
        this.add(new IndexMarkerCraftedEntry(this).generate()
                .markerUntilCrafted(Items.DIAMOND_SWORD));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withDisplayMode(BookDisplayMode.INDEX);
    }

    @Override
    protected String categoryName() {
        return "Index Markers Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
