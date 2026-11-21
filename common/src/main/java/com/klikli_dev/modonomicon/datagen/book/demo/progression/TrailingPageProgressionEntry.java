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
                Requirements are on a trailing page after the last page. Flip past the end to see the progress bars.
                """);

        this.stage("paper", s -> s
                .value(DemoResearch.PAPER_COLLECTED, 5)
                .icon(Items.PAPER)
                .title("Collect paper")
                .description("Collect 5 paper."));

        this.stage("sources", s -> s
                .value(DemoResearch.SOURCES_CONSULTED, 1)
                .icon(Items.BOOK)
                .title("Consult a source")
                .description("View the Sources Shelf."));

        this.page("catalogued", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Catalogued");
        this.pageText("""
                Stage-gated page: visible once both stages are complete.
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
