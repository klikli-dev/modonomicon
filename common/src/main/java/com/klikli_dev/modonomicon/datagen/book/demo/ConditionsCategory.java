/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookTrueConditionModel;
import com.klikli_dev.modonomicon.datagen.book.demo.acquiring.AcquiringInkSacEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.acquiring.AcquiringIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.crafting.CraftingIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.crafting.CraftingNoteEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionAdvancementEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionLevel1Entry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionLevel2Entry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionNotEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.RequirementsEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.TwoParentEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerAcquiredEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerCraftedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerExplicitEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.markers.MarkerViewedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.ChildProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.FirstPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.NoneProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.PlacedPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.progression.TrailingPageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageCompleteEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorAEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorBEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCompleteEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * The merged conditions and progression category. Each band of the layout is one storyline: conditions on the top row,
 * progressions on the second row, then crafting and acquiring with their collectors and markers.
 */
public class ConditionsCategory extends CategoryProvider {
    public static final String ID = "conditions";

    public ConditionsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        //condition band (top row): root, catalogue (node gated), index (entry gated), two parents, advancement, not
        var conditionRootEntry = this.add(new ConditionRootEntry(this).generate());
        this.layout().entry(conditionRootEntry).at(0, 0);

        //the condition for the level 1 entry to depend on the root entry is set up here so we can access the entry. We could also do it in the entry provider and either hand over a reference, or use the ID as resource location to reference it
        var conditionLevel1Entry = this.add(new ConditionLevel1Entry(this).generate())
                //here we use this.parent() to get access to the parent settings
                .withParent(this.parent(conditionRootEntry).withLineReversed(true))
                //use researchNodeEntryViewedOnce helper to show "Requires entry ... to be unlocked" tooltip instead of default "Requires research node ..."
                .withCondition(this.condition().researchNodeEntryViewedOnce(DemoResearch.CONDITION_LEVEL_1, conditionRootEntry));
        this.layout().entry(conditionLevel1Entry).at(3, 0);

        var conditionLevel2Entry = this.add(new ConditionLevel2Entry(this).generate())
                //here we use this.parent() to get access to the parent settings
                .withParent(conditionLevel1Entry)
                //use entryViewedOnce to auto-generate research: requires conditionLevel1Entry to be viewed
                .withCondition(this.condition().entryViewedOnce(conditionLevel1Entry));
        this.layout().entry(conditionLevel2Entry).at(6, 0);

        var twoParentsEntry = this.add(new TwoParentEntry(this).generate())
                .showWhenAnyParentUnlocked(true)
                .withParent(this.parent(conditionRootEntry).withLineReversed(true))
                .withParent(conditionLevel2Entry);
        this.layout().entry(twoParentsEntry).at(6, 2);

        //the advancement condition is set up in the entry provider
        var conditionAdvancementEntry = this.add(new ConditionAdvancementEntry(this).generate());
        this.layout().entry(conditionAdvancementEntry).at(9, 0);

        var conditionNotEntry = this.add(new ConditionNotEntry(this).generate());
        this.layout().entry(conditionNotEntry).at(12, 0);

        //progression band (second row): first page with its chain, then the scholarship stage progressions
        var firstPage = this.add(new FirstPageProgressionEntry(this).generate());
        this.layout().entry(firstPage).at(0, 5);

        var child = this.add(new ChildProgressionEntry(this).generate())
                .withParent(firstPage);
        this.layout().entry(child).at(3, 5);

        var trailingPage = this.add(new TrailingPageProgressionEntry(this).generate())
                .withParent(firstPage);
        this.layout().entry(trailingPage).at(0, 7);

        var placedPage = this.add(new PlacedPageProgressionEntry(this).generate())
                .withParent(trailingPage);
        this.layout().entry(placedPage).at(0, 9);

        var none = this.add(new NoneProgressionEntry(this).generate())
                .withParent(placedPage);
        this.layout().entry(none).at(0, 11);

        //independent chain: the bookbinding stages with a stage complete entry that unlocks once the final stage is reached
        var stageProgression = this.add(new StageProgressionEntry(this).generate());
        this.layout().entry(stageProgression).at(6, 5);

        var stageComplete = this.add(new StageCompleteEntry(this).generate()
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_3.id()))
                .withParent(stageProgression));
        this.layout().entry(stageComplete).at(6, 7);

        var requirements = this.add(new RequirementsEntry(this).generate());
        this.layout().entry(requirements).at(9, 5);

        //crafting and acquiring chains (top right): a completed note and ink, each unlocking its entry
        var craftingIntroEntry = this.add(new CraftingIntroEntry(this).generate());
        this.layout().entry(craftingIntroEntry).at(15, 0);

        var craftingNoteEntry = this.add(new CraftingNoteEntry(this).generate()
                .withCondition(DemoResearch.CRAFTING_NOTE)
                .withParent(craftingIntroEntry));
        this.layout().entry(craftingNoteEntry).at(18, 0);

        var acquiringIntroEntry = this.add(new AcquiringIntroEntry(this).generate());
        this.layout().entry(acquiringIntroEntry).at(15, 5);

        var acquiringInkSacEntry = this.add(new AcquiringInkSacEntry(this).generate()
                .withCondition(DemoResearch.ACQUIRE_INK_SAC)
                .withParent(acquiringIntroEntry));
        this.layout().entry(acquiringInkSacEntry).at(18, 5);

        //value chain: each source viewed counts one, the bibliography unlocks once all three are consulted
        var collectorAEntry = this.add(new CollectorAEntry(this).generate());
        this.layout().entry(collectorAEntry).at(9, 10);

        var collectorBEntry = this.add(new CollectorBEntry(this).generate());
        this.layout().entry(collectorBEntry).at(10, 10);

        var collectorCEntry = this.add(new CollectorCEntry(this).generate());
        this.layout().entry(collectorCEntry).at(11, 10);

        var collectorCompleteEntry = this.add(new CollectorCompleteEntry(this).generate()
                .withCondition(DemoResearch.BIBLIOGRAPHY_COMPLETE));
        this.layout().entry(collectorCompleteEntry).at(10, 12);

        var markerIntroEntry = this.add(new MarkerIntroEntry(this).generate());
        this.layout().entry(markerIntroEntry).at(15, 10);

        //All marker demo entries stay explicitly unlocked (always-true condition opts them out of the
        //automatic parent-hierarchy research), so the marker dot - not an unlock gate - is what is on display.
        var alwaysUnlocked = BookTrueConditionModel.create();

        //explicit marker condition pointing at a manually authored research node
        var markerExplicitEntry = this.add(new MarkerExplicitEntry(this).generate()
                .withCondition(alwaysUnlocked)
                .withMarker(DemoResearch.MARKER_EXPLICIT)
                .withParent(markerIntroEntry));
        this.layout().entry(markerExplicitEntry).at(18, 10);

        //auto-generated marker research: dot clears once the intro entry has been viewed
        var markerViewedEntry = this.add(new MarkerViewedEntry(this).generate()
                .withCondition(alwaysUnlocked)
                .markerUntilEntryViewed(markerIntroEntry)
                .withParent(markerIntroEntry));
        this.layout().entry(markerViewedEntry).at(15, 13);

        //auto-generated marker research: dot clears once a writable book has been crafted
        var markerCraftedEntry = this.add(new MarkerCraftedEntry(this).generate()
                .withCondition(alwaysUnlocked)
                .markerUntilCrafted(Items.WRITABLE_BOOK)
                .withParent(markerIntroEntry));
        this.layout().entry(markerCraftedEntry).at(18, 13);

        //same as above, but authored through the ConditionHelper instead of the entry model
        var markerAcquiredEntry = this.add(new MarkerAcquiredEntry(this).generate()
                .withCondition(alwaysUnlocked)
                .withMarkerCondition(this.condition().markerUntilAcquired(Items.IRON_INGOT))
                .withParent(markerIntroEntry));
        this.layout().entry(markerAcquiredEntry).at(15, 16);
    }

    @Override
    protected String categoryName() {
        return "Conditions & Progression";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.ENCHANTED_BOOK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
