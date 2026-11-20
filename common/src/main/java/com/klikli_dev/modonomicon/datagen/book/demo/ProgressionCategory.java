// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.datagen.book.demo.features.RequirementsEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.ChildProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.FirstPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.NoneProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.PlacedPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.TrailingPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageCompleteEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class ProgressionCategory extends CategoryProvider {
    public static final String ID = "progression";

    public ProgressionCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        //each entry is a parent of the next one, so the completion of a progression unlocks the entry after it
        var firstPage = this.add(new FirstPageProgressionEntry(this).generate());
        this.layout().entry(firstPage).at(0, 0);

        var child = this.add(new ChildProgressionEntry(this).generate())
                .withParent(firstPage);
        this.layout().entry(child).rightOf(firstPage, 3);

        var trailingPage = this.add(new TrailingPageProgressionEntry(this).generate())
                .withParent(firstPage);
        this.layout().entry(trailingPage).below(firstPage, 2);

        var placedPage = this.add(new PlacedPageProgressionEntry(this).generate())
                .withParent(trailingPage);
        this.layout().entry(placedPage).below(trailingPage, 2);

        var none = this.add(new NoneProgressionEntry(this).generate())
                .withParent(placedPage);
        this.layout().entry(none).below(placedPage, 2);

        //independent chain: the bookbinding stages with a stage complete entry that unlocks once the final stage is reached
        var stageProgression = this.add(new StageProgressionEntry(this).generate());
        this.layout().entry(stageProgression).at(7, 0);

        var stageComplete = this.add(new StageCompleteEntry(this).generate()
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_3.id()))
                .withParent(stageProgression));
        this.layout().entry(stageComplete).below(stageProgression, 2);

        var requirements = this.add(new RequirementsEntry(this).generate());
        this.layout().entry(requirements).at(11, 0);
    }

    @Override
    protected String categoryName() {
        return "Progression Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.CLOCK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
