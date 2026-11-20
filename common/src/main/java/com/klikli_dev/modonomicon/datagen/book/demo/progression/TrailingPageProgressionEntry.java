// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Trailing page progression: its requirements are listed on a trailing page after the last page. Value requirements are shown as progress bars.
 */
public class TrailingPageProgressionEntry extends EntryProvider {
    public static final String ID = "trailing_page_progression";

    public TrailingPageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.requirementsDisplay(RequirementsDisplay.TRAILING_PAGE);

        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Trailing page requirements");
        this.pageText("""
                The requirements of this entry are listed on a trailing page after the last page of the entry. Flip past the end of the entry to see them.
                The first stage counts the paper you pick up, the second counts the sources you consulted on the shelf. The progress of each is shown as a bar.
                """);

        this.stage("paper", s -> s
                .value(DemoResearch.PAPER_COLLECTED, 5)
                .icon(Items.PAPER)
                .title("Gather paper")
                .description("Pick up 5 paper."));

        this.stage("sources", s -> s
                .value(DemoResearch.SOURCES_CONSULTED, 1)
                .icon(Items.BOOK)
                .title("Consult a source")
                .description("Read the sources shelf of this category."));

        this.page("catalogued", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Catalogued");
        this.pageText("""
                Both stages are complete, so the index is unlocked. Once the last stage is done, the trailing page only lists completed requirements.
                """);
    }

    @Override
    protected String entryName() {
        return "Catalogue";
    }

    @Override
    protected String entryDescription() {
        return "Trailing page progression with value requirements.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
