// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.research;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookStageRequirementsPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Hand-authored research: the research is declared in {@link DemoResearch}, the entry has no requirements display and
 * shows its progress with requirement pages placed in the content, and its pages are gated by the stages.
 */
public class NoneResearchEntry extends EntryProvider {
    public static final String ID = "none_research";

    public NoneResearchEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        entry.withResearch(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2);
        return super.additionalSetup(entry);
    }

    @Override
    protected void generatePages() {
        this.requirementsDisplay(RequirementsDisplay.NONE);

        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hand-authored requirements");
        this.pageText("""
                No requirements display: the requirements pages below show each stage instead.
                """);

        this.page("stage_1_requirements", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.REQUIREMENTS_STAGE_1));
        this.pageTitle("Stage 1: Ink");
        this.pageText("""
                Get an ink sac.
                """);

        this.page("after_ink", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_1)));
        this.pageTitle("Ink gathered");
        this.pageText("""
                Stage-gated page: visible once stage 1 is complete.
                """);

        this.page("stage_2_requirements", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.REQUIREMENTS_STAGE_2));
        this.pageTitle("Stage 2: Iron and paper");
        this.pageText("""
                Get an iron ingot and collect 5 paper.
                """);

        this.page("after_iron", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2)));
        this.pageTitle("Iron and paper collected");
        this.pageText("""
                Stage-gated page: visible once stage 2 is complete. The next entry, Bookbinding, is unlocked by this one.
                """);
    }

    @Override
    protected String entryName() {
        return "Hand-authored Requirements";
    }

    @Override
    protected String entryDescription() {
        return "Hand-authored stages with requirement pages, no requirements display.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GLASS);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
