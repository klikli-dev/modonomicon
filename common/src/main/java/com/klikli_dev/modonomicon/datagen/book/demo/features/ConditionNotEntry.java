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

public class ConditionNotEntry extends EntryProvider {
    public static final String ID = "condition_not";

    public ConditionNotEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        // Hint page: visible only before the Mine Stone advancement research unlocks.
        // Demonstrates the "not" condition swapping content once a condition is met (see #151).
        this.page("hint", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().not(
                        this.condition().researchNodeUnlocked(DemoResearch.ADVANCEMENT_MINE_STONE)))
        );
        this.pageTitle("Not Yet Mined");
        this.pageText("""
                Hint: mine stone to unlock the detailed version of this entry.
                This page disappears once the condition is met.
                """);

        // Detailed page: visible only after the Mine Stone advancement research unlocks.
        this.page("revealed", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchNodeUnlocked(DemoResearch.ADVANCEMENT_MINE_STONE))
        );
        this.pageTitle("Stone Mined!");
        this.pageText("""
                You mined stone and revealed the final version of this entry.
                """);
    }

    @Override
    protected String entryName() {
        return "Not Condition Entry";
    }

    @Override
    protected String entryDescription() {
        return "Swaps pages when a condition is met.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BARRIER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
