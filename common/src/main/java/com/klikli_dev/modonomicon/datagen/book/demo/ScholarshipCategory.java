/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookTrueConditionModel;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionAdvancementEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.TwoParentEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.research.FirstPageResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.research.NoneResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.research.PlacedPageResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.research.TrailingPageResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.scholarship.ScholarshipEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageResearchEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.SourcesEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * The demo storyline. Every entry unlocks the next one once its research is complete, and its stage rows show what
 * is still missing. The Sources shelf is the only ungated entry besides the root, it is read to count sources.
 */
public class ScholarshipCategory extends CategoryProvider {
    public static final String ID = "scholarship";

    public ScholarshipCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        var scholarship = this.add(new ScholarshipEntry(this).generate());
        this.layout().entry(scholarship).at(0, 0);

        //ungated shelf: viewing it counts one source consulted, the catalogue requires one
        var sources = this.add(new SourcesEntry(this).generate()
                .withCondition(BookTrueConditionModel.create()));
        this.layout().entry(sources).at(6, 3);

        //first page research with the requirements on the first page, child of the root
        var desk = this.add(new FirstPageResearchEntry(this).generate()
                .withParent(scholarship)
                .markerUntilEntryViewed(sources));
        this.layout().entry(desk).rightOf(scholarship, 3);

        //trailing page research: value requirements (paper, sources) listed on a trailing page
        var catalogue = this.add(new TrailingPageResearchEntry(this).generate()
                .withParent(desk));
        this.layout().entry(catalogue).rightOf(desk, 3);

        //placed requirement pages, plus an entry viewed once condition as entry condition.
        //the condition replaces the parent gate, so the catalogue's final stage is required explicitly.
        var index = this.add(new PlacedPageResearchEntry(this).generate()
                .withParent(catalogue)
                .withCondition(this.condition().and(
                        this.research().entryViewedOnce(catalogue),
                        this.condition().researchStageCompleted(
                                this.research().entryResearchNode(catalogue),
                                this.research().entryResearchStage(catalogue, "sources"))))
                .markerUntilCrafted(Items.WRITABLE_BOOK));
        this.layout().entry(index).rightOf(catalogue, 3);

        //two parents: shown as soon as either the first page entry or the index is unlocked
        var twoParents = this.add(new TwoParentEntry(this).generate()
                .showWhenAnyParentUnlocked(true)
                .withParent(desk)
                .withParent(index)
                .markerUntilAcquired(Items.DIAMOND));
        this.layout().entry(twoParents).rightOf(index, 3);

        //advancement condition: the entry is gated by an advancement, the two parents entry asks for it
        var advancement = this.add(new ConditionAdvancementEntry(this).generate()
                .withParent(twoParents));
        this.layout().entry(advancement).rightOf(twoParents, 3);

        //hand-authored stages with pages gated by the stages, explicit marker cleared by viewing the index
        var none = this.add(new NoneResearchEntry(this).generate()
                .withParent(advancement)
                .withMarker(DemoResearch.MARKER_EXPLICIT));
        this.layout().entry(none).rightOf(advancement, 3);

        //stage research with pages gated by stages, the last page is gated by the stage complete condition
        var stages = this.add(new StageResearchEntry(this).generate()
                .withParent(none));
        this.layout().entry(stages).rightOf(none, 3);
    }

    @Override
    protected String categoryName() {
        return "Scholarship";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
