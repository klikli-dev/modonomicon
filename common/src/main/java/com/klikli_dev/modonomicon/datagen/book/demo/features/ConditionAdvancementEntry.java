// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
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
        var pageCondition = BookAdvancementConditionModel.create()
                .withAdvancementId(ResourceLocation.parse("minecraft:story/mine_stone"));
        this.lang().add(
                Util.makeDescriptionId("advancement", pageCondition.getAdvancementId()) + ".title",
                "Mine Stone"
        );

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
        var advancementCondition = this.condition().advancement(ResourceLocation.parse("minecraft:husbandry/ride_a_boat_with_a_goat"));
        this.lang().add(
                Util.makeDescriptionId("advancement", advancementCondition.getAdvancementId()) + ".title",
                "Ride a Boat with a Goat"
        );

        return entry.withCondition(advancementCondition);
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
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(1, 0);
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
