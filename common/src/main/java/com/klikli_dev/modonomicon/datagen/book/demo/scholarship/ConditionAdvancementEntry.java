// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.scholarship;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

/**
 * Gated by an advancement on the entry itself. Its pages demonstrate the not and node unlocked conditions, and its stage asks for the next advancement.
 */
public class ConditionAdvancementEntry extends EntryProvider {
    public static final String ID = "condition_advancement";

    public ConditionAdvancementEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        //the entry itself is unlocked by the advancement, see the two parents entry for the stage that asks for it
        return entry.withCondition(DemoResearch.ADVANCEMENT_RIDE_BOAT_WITH_GOAT);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Field work");
        this.pageText("""
                Gated by the advancement Ride a Boat with a Goat (entry condition).
                """);

        //not condition: the hint is shown until the mine stone research node is unlocked
        this.page("hint", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().not(
                        this.condition().researchNodeUnlocked(DemoResearch.ADVANCEMENT_MINE_STONE))));
        this.pageTitle("Hint");
        this.pageText("""
                Not condition: mine a stone block to earn Mine Stone. This hint hides once it is unlocked.
                """);

        //node unlocked condition: the page is only shown once the mine stone research node is unlocked
        this.page("conditional_page", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchNodeUnlocked(DemoResearch.ADVANCEMENT_MINE_STONE)));
        this.pageTitle("Quarried");
        this.pageText("""
                Node unlocked condition: visible once Mine Stone is unlocked.
                """);

        this.stage("mine", s -> s
                .fact(DemoResearch.ADVANCEMENT_MINE_STONE_COMPLETED)
                .icon(Items.STONE_PICKAXE)
                .title("Mine stone")
                .description("Earn the advancement Mine Stone."));
    }

    @Override
    protected String entryName() {
        return "Field Work";
    }

    @Override
    protected String entryDescription() {
        return "Gated by an advancement, with not and node conditions on its pages.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.NETHER_STAR);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
