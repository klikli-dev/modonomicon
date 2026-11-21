// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.research;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Placed page research: requirements pages are placed between the content, they keep showing progress after a stage is complete.
 */
public class PlacedPageResearchEntry extends EntryProvider {
    public static final String ID = "placed_page_research";

    public PlacedPageResearchEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Placed requirements");
        this.pageText("""
                Requirements pages are placed in the content. They stay visible and show check marks once a stage is complete.
                The marker of this entry clears once you have crafted a writable book.
                """);

        this.requirementsPage("requirements_iron");

        this.stage("iron", s -> s
                .fact(DemoResearch.IRON_INGOT_ACQUIRED)
                .icon(Items.IRON_INGOT)
                .title("Iron ingot")
                .description("Get an iron ingot."));

        this.page("iron_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron acquired");
        this.pageText("""
                Stage-gated page: visible once you have the iron ingot.
                """);

        this.requirementsPage("requirements_ink");

        this.stage("ink", s -> s
                .fact(DemoResearch.INK_SAC_ACQUIRED)
                .icon(Items.INK_SAC)
                .title("Ink sac")
                .description("Get an ink sac."));

        this.page("ink_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ink gathered");
        this.pageText("""
                Both stages are complete. The next entry, Cross-reference, asks for an advancement.
                """);
    }

    @Override
    protected String entryName() {
        return "Index";
    }

    @Override
    protected String entryDescription() {
        return "Placed requirements pages between the content.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.TORCH);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
