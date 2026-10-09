/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerAcquiredEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerCraftedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerExplicitEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerViewedEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class MarkersCategory extends CategoryProvider {
    public static final String ID = "markers";

    public MarkersCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(MarkerIntroEntry.ID).at(0, 0);
        layout.entry(MarkerExplicitEntry.ID).at(3, 0);
        layout.entry(MarkerViewedEntry.ID).at(0, 3);
        layout.entry(MarkerCraftedEntry.ID).at(3, 3);
        layout.entry(MarkerAcquiredEntry.ID).at(0, 6);
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new MarkerIntroEntry(this).generate());

        //explicit marker condition pointing at a manually authored research node
        this.add(new MarkerExplicitEntry(this).generate()
                        .withMarker(DemoResearch.MARKER_EXPLICIT))
                .withParent(introEntry);

        //auto-generated marker research: dot clears once the intro entry has been viewed
        this.add(new MarkerViewedEntry(this).generate()
                        .markerUntilEntryViewed(introEntry))
                .withParent(introEntry);

        //auto-generated marker research: dot clears once a stick has been crafted
        this.add(new MarkerCraftedEntry(this).generate()
                        .markerUntilCrafted(Items.STICK))
                .withParent(introEntry);

        //same as above, but authored through the ConditionHelper instead of the entry model
        this.add(new MarkerAcquiredEntry(this).generate()
                        .withMarkerCondition(this.condition().markerUntilAcquired(Items.COBBLESTONE)))
                .withParent(introEntry);
    }

    @Override
    protected String categoryName() {
        return "Markers Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.TORCH);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
