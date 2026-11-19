// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class NoneProgressionEntry extends EntryProvider {
    public static final String ID = "none_progression";

    public NoneProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        //the research is hand-authored in DemoResearch, the entry only shows its content gated by its stages
        entry.withProgression(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2);
        return super.additionalSetup(entry);
    }

    @Override
    protected void generatePages() {
        this.requirementsDisplay(RequirementsDisplay.NONE);

        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("No requirements display");
        this.pageText("""
                This entry has no requirements display, so nothing is listed on its pages.
                Its research is hand-authored: the first page below needs an oak log, the last page needs iron ingots and 5 cobblestone.
                Pages after a stage are only visible once the stage is complete, the content is gated without a block.
                """);

        this.page("after_oak_log", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_1)));
        this.pageTitle("Oak log collected");
        this.pageText("""
                You collected an oak log, so this page is visible. This is the hand-authored stage 1 of the requirements research.
                """);

        this.page("after_iron", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchStageCompleted(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2)));
        this.pageTitle("Iron and cobblestone collected");
        this.pageText("""
                The last stage is complete. Its requirement is the iron ingot and 5 cobblestone of the requirements research.
                """);
    }

    @Override
    protected String entryName() {
        return "None Progression";
    }

    @Override
    protected String entryDescription() {
        return "A hand-authored progression with content gated by stages, but no requirements display.";
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
