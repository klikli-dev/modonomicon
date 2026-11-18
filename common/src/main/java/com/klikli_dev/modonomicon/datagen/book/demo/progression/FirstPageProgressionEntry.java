// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.book.demo.ResearchCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;
import net.minecraft.world.item.Items;

public class FirstPageProgressionEntry extends EntryProvider {
    public static final String ID = "first_page_progression";

    public FirstPageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("First page requirements");
        this.pageText("""
                This is the default display: the requirements of the next stage are listed at the bottom of this first page.
                Stage 1 needs a crafting table and an oak log. Each requirement gets a check mark once you have it.
                Until the stage is complete, the pages and tabs after it stay hidden.
                """);

        this.tab("notes", () -> BookEntryTabModel.create()
                .withPage(BookTextPageModel.create()
                        .withTitle(this.tabTitleKey())
                        .withText(this.tabTextKey()))
                .withIcon(Items.WRITABLE_BOOK)
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Notes");
        this.tabText("""
                This tab is pinned to the first page. It is visible from the start, because it comes before the first stage.
                """);

        this.stage("workbench", s -> s
                .craft(Items.CRAFTING_TABLE)
                .obtain(Items.OAK_LOG)
                .title("Gathering wood")
                .description("Craft a crafting table and collect an oak log."));

        this.page("workbench_unlocked", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Workbench unlocked");
        this.pageText("""
                You have a crafting table and an oak log, so stage 1 is complete and this page was unlocked.
                The tab below is pinned to this page and is gated by stage 1 as well.
                """);

        this.tab("workbench_notes", () -> BookEntryTabModel.create()
                .withPage(BookTextPageModel.create()
                        .withTitle(this.tabTitleKey())
                        .withText(this.tabTextKey()))
                .withIcon(Items.CRAFTING_TABLE)
                .withTitle(this.tabTitleKey())
        );
        this.tabTitle("Workbench notes");
        this.tabText("""
                Tabs declared after a stage are gated by that stage, just like pages. This tab only shows up after stage 1 is complete.
                """);

        this.stage("reading", s -> s
                .read(this.modLoc(ResearchCategory.ID + "/" + ConditionRootEntry.ID))
                .title("Reading the features")
                .description("Read the Condition Root entry in the Features category."));

        this.page("finished", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Finished");
        this.pageText("""
                Both stages are complete, so this page is visible. The entry is complete, which unlocks the child entry of this category.
                """);
    }

    @Override
    protected String entryName() {
        return "First Page Progression";
    }

    @Override
    protected String entryDescription() {
        return "Staged progression with the requirements on the first page, the default display.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
