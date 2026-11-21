// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

/**
 * First page progression: its requirements are listed at the bottom of the first page (the default display). It is the child of the scholarship entry.
 */
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
        this.pageTitle("Setting up the desk");
        this.pageText("""
                Requirements at the bottom of the page: craft a crafting table and collect any log. Check marks show progress.
                The marker on this entry clears once you have read the sources shelf.
                """);

        this.stage("desk", s -> s
                .craft(Items.CRAFTING_TABLE)
                .obtain(ItemTags.LOGS)
                .title("Set up the desk")
                .description("Craft a crafting table and collect a log."));

        this.page("finished", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Desk ready");
        this.pageText("""
                Stage-gated page: only visible once the desk is set up.
                """);
    }

    @Override
    protected String entryName() {
        return "Setting up the desk";
    }

    @Override
    protected String entryDescription() {
        return "First page progression, unlocked by the scholarship entry.";
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
