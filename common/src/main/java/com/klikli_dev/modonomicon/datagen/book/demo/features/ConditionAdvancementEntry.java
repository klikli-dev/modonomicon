// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class ConditionAdvancementEntry extends EntryProvider {
    public static final String ID = "condition_advancement";

    public ConditionAdvancementEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Advancement Condition");
        this.pageText("""
                Advancement Conditions unlock, as the name implies, if a player has an advancement.
                """);

        //set up a condition for a conditional page
        var pageCondition = this.condition().researchNodeUnlocked(Identifier.parse("modonomicon:demo/advancement_mine_stone"));

        this.page("conditional_page", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(pageCondition)
        );
        this.pageTitle("Conditional Condition");
        this.pageText("""
                Conditional pages unlock if a player has satisfied their condition.
                """);
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        //Set up the condition for the entry.
        return entry.withCondition(this.condition().researchNodeUnlocked(Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat")));
    }

    @Override
    protected String entryName() {
        return "Advancement Condition Entry";
    }

    @Override
    protected String entryDescription() {
        return "Depends on an advancement being unlocked.";
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

