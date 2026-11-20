// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

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
 * Hand-authored progression: the research is declared in {@link DemoResearch}, the entry has no requirements display and
 * shows its progress with requirement pages placed in the content, and its pages are gated by the stages.
 */
public class NoneProgressionEntry extends EntryProvider {
    public static final String ID = "none_progression";

    public NoneProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        entry.withProgression(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2);
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
                This entry has no requirements display, so nothing is listed at the bottom of its pages.
                Its research is hand-authored: the first stage needs an ink sac, the second needs an iron ingot and 5 paper.
                The requirements pages below show the progress of each stage instead, and the pages after a stage stay hidden until it is complete.
                """);

        this.page("stage_1_requirements", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.REQUIREMENTS_STAGE_1));

        this.page("after_ink", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_1)));
        this.pageTitle("Ink gathered");
        this.pageText("""
                You gathered an ink sac, so this page is visible. This is the hand-authored stage 1 of the requirements research.
                """);

        this.page("stage_2_requirements", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.REQUIREMENTS_STAGE_2));

        this.page("after_iron", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2)));
        this.pageTitle("Iron and paper collected");
        this.pageText("""
                The last stage is complete. Its requirements were the iron ingot and 5 paper of the requirements research.
                The next entry, Bookbinding, is unlocked by this one.
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
