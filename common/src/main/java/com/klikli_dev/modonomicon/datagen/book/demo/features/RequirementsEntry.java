// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookStageRequirementsPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class RequirementsEntry extends EntryProvider {
    public static final String ID = "requirements";

    public RequirementsEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        //hand-authored example: the stages of this node are declared in DemoResearch, see the progression entries for inline stages
        entry.withProgression(DemoResearch.REQUIREMENTS_STAGED, DemoResearch.REQUIREMENTS_STAGE_2);
        return super.additionalSetup(entry);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Requirements");
        this.pageText("""
                The requirements at the bottom of this page list what is needed to understand this entry. Completed requirements get a check mark.
                Requirements are grouped into categories, one row each: research, items, knowledge and progress. The demo category Rituals gets its own row for the iron ingot.
                Below the rows, the stage line shows the stage that is required. Use its arrows to look at the stages you have already reached.
                """);

        this.page("stage_requirements", () -> BookStageRequirementsPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withStage(DemoResearch.REQUIREMENTS_STAGE_1)
        );
        this.pageTitle("Stage requirements");
        this.pageText("""
                This page shows the requirements of the first stage of the staged research, whatever the requirements display of the book is.
                """);
    }

    @Override
    protected String entryName() {
        return "Requirements";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing the requirements at the bottom of the first page and the requirements page.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
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
