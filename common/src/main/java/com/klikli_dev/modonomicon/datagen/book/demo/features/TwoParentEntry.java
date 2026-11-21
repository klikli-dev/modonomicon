// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * An entry with two parents. It shows up as soon as any of its parents is unlocked, and it asks for an advancement that unlocks the next entry.
 */
public class TwoParentEntry extends EntryProvider {
    public static final String ID = "two_parents";

    public TwoParentEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Cross-reference");
        this.pageText("""
                Two parents: shown once either the desk or the index is unlocked. The marker clears once you acquire a diamond.
                """);

        this.stage("ride", s -> s
                .fact(DemoResearch.ADVANCEMENT_RIDE_BOAT_WITH_GOAT_COMPLETED)
                .icon(Items.OAK_BOAT)
                .title("Ride a boat with a goat")
                .description("Earn the advancement Ride a Boat with a Goat."));

        this.page("cross_referenced", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Cross-referenced");
        this.pageText("""
                Field Work is unlocked. It is gated by an advancement.
                """);
    }

    @Override
    protected String entryName() {
        return "Cross-reference";
    }

    @Override
    protected String entryDescription() {
        return "Entry with two parents, visible once any parent is unlocked.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.AMETHYST_CLUSTER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
