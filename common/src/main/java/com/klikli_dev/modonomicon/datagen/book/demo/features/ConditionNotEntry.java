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
        // Hint page: visible only before the stick crafting research unlocks.
        // Demonstrates the "not" condition swapping content once a condition is met (see #151).
        // Uses a repeatable item_crafted trigger (instead of a one-shot advancement)
        // so the swap can be re-tested after every research reset.
        this.page("hint", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().not(
                        this.condition().researchNodeUnlocked(DemoResearch.CRAFTING_STICK)))
        );
        this.pageTitle("Not Yet Crafted");
        this.pageText("""
                Hint: craft a stick to unlock the detailed version of this entry.
                This page disappears once the condition is met.
                """);

        // Detailed page: visible only after the stick crafting research unlocks.
        this.page("revealed", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(this.condition().researchNodeUnlocked(DemoResearch.CRAFTING_STICK))
        );
        this.pageTitle("Stick Crafted!");
        this.pageText("""
                You crafted a stick and revealed the final version of this entry.
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
