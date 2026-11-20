// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.stages;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookStageRequirementsPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Research stages: pages are gated by the stages of a value based node, and the last page is gated by the stage complete condition.
 */
public class StageProgressionEntry extends EntryProvider {
    public static final String ID = "stage_progression";

    public StageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bookbinding");
        this.pageText("""
                This entry demonstrates research stages. Each page unlocks as you complete a stage by binding writable books, which needs ink.
                The requirements page below shows the stage that is next, with its progress.
                """);

        this.page("progress", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.WRITABLE_BOOK_STAGE_1));

        this.page("stage_1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_1.id())));
        this.pageTitle("Stage 1 Complete");
        this.pageText("""
                You bound your first writable book and completed stage 1.
                Bind 2 more (3 total) to unlock the next stage.
                """);

        this.page("stage_2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(DemoResearch.WRITABLE_BOOK_STAGES.id())
                        .withStage(DemoResearch.WRITABLE_BOOK_STAGE_2.id())));
        this.pageTitle("Stage 2 Complete");
        this.pageText("""
                Stage 2 is done. You have bound 3 writable books.
                Bind 2 more (5 total) to reach the final stage.
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
                All research stages have been completed. The bound volume is finished, and this last page only shows once the final stage is complete.
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
