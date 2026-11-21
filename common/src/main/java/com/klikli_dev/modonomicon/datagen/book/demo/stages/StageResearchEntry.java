// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.stages;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookScholarshipPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Research stages: pages are gated by the stages of a value based node, and the last page is gated by the stage complete condition.
 */
public class StageResearchEntry extends EntryProvider {
    public static final String ID = "stage_research";

    public StageResearchEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bookbinding");
        this.pageText("""
                Bind writable books to unlock pages. Craft writable books to progress. The recipe is in the tab beside this page.
                """);

        this.tab("writable_book_recipe", () -> BookEntryTabModel.create()
                .withPage(BookCraftingRecipePageModel.create().withRecipeId1("minecraft:writable_book"))
                .withTitle(this.tabTitleKey()));
        this.tabTitle("Writable book recipe");

        this.page("progress", () -> BookScholarshipPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.WRITABLE_BOOK_STAGE_1));
        this.pageTitle("Stage 1: First book");
        this.pageText("""
                Craft 1 writable book.
                """);

        this.page("stage_1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_1.id())));
        this.pageTitle("Stage 1 Complete");
        this.pageText("""
                Stage-gated page: visible once 1 book is crafted.
                """);

        this.page("progress_2", () -> BookScholarshipPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.WRITABLE_BOOK_STAGE_2));
        this.pageTitle("Stage 2: Three books");
        this.pageText("""
                Craft 3 writable books in total.
                """);

        this.page("stage_2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_2.id())));
        this.pageTitle("Stage 2 Complete");
        this.pageText("""
                Stage-gated page: visible once 3 books are crafted.
                """);

        this.page("progress_3", () -> BookScholarshipPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.WRITABLE_BOOK_STAGE_3));
        this.pageTitle("Stage 3: Five books");
        this.pageText("""
                Craft 5 writable books in total.
                """);

        //stage complete condition: the last page is only shown once the final stage is complete
        this.page("stage_3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_3.id())));
        this.pageTitle("Bound volume");
        this.pageText("""
                Stage-gated page: visible once 5 books are crafted.
                """);
    }

    @Override
    protected String entryName() {
        return "Bookbinding";
    }

    @Override
    protected String entryDescription() {
        return "Bind writable books to unlock each page.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
