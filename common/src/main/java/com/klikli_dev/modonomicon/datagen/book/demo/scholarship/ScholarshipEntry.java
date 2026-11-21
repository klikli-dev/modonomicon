// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.scholarship;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchActionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCostModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchEffectModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * The entry point of the book: a staged progression demonstrating research actions, the take, process and submit loop of an item_used fact, rewards and manual completion.
 */
public class ScholarshipEntry extends EntryProvider {
    public static final String ID = "notes";
    public static final Identifier SYNTHESIS_FUNCTION = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/scholarship_synthesis");
    /**
     * The demo recipe that processes a blank research note into a completed one, see {@code data/modonomicon/recipe/crafting/demo_research_note.json}.
     * Vanilla ingredients ignore components, so plain paper also processes into a completed note. The demo accepts this loophole on purpose.
     */
    public static final String RESEARCH_NOTE_RECIPE = "modonomicon:crafting/demo_research_note";

    public ScholarshipEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Scholarship");
        this.pageText("""
                Complete each stage to unlock the next entry. The requirements of the current stage are listed at the bottom of each page.
                """);

        this.stage("observation", s -> s
                .action("observation", "Record your observations",
                        ResearchActionModel.learn(ResearchCostModel.create().item(Items.PAPER)))
                .title("Observation")
                .description("Pay 1 paper with the action button.")
                .reward(ResearchEffectModel.experience(10)));

        this.page("observation_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Actions");
        this.pageText("""
                Click an action icon to perform it. Hover it to see its costs.
                """);

        this.page("transcription_take", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Take a blank note");
        this.pageText("""
                Repeatable action: costs 1 paper and 1 shears durability per note.
                """);

        this.page("transcription_process", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Make a completed note");
        this.pageText("""
                Craft the blank note with an ink sac. The recipe is in the tab beside this page.
                """);

        this.tab("research_note_recipe", () -> BookEntryTabModel.create()
                .withPage(BookCraftingRecipePageModel.create().withRecipeId1(RESEARCH_NOTE_RECIPE))
                .withTitle(this.tabTitleKey()));
        this.tabTitle("Research note recipe");

        this.page("transcription_submit", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Study");
        this.pageText("""
                Use the completed note in the air to study it. The note is consumed.
                """);

        this.stage("transcription", s -> s
                .fact(DemoResearch.SCHOLARSHIP_NOTE_TAKEN)
                .fact(DemoResearch.SCHOLARSHIP_NOTE_CRAFTED)
                .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
                .title("Transcription")
                .description("Take a blank note, craft it with an ink sac, then use the completed note in the air.")
                .reward(ResearchEffectModel.addValue(DemoResearch.SCHOLARSHIP_INSIGHT, 1))
                .reward(ResearchEffectModel.grantFact(DemoResearch.SCHOLARSHIP_NOTES_READ)));

        //the study fact of transcription is already granted, so this stage only consumes its cost
        this.stage("synthesis", s -> s
                .consume(Items.IRON_INGOT, 2)
                .title("Synthesis")
                .description("Hold 2 iron ingots, then press Complete.")
                .reward(ResearchEffectModel.giveItem(Items.EMERALD))
                .reward(ResearchEffectModel.function(SYNTHESIS_FUNCTION)));

        this.page("synthesis_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Manual completion");
        this.pageText("""
                Press the Complete button of the stage line once you hold 2 iron ingots. The ingots are consumed, and you get an emerald.
                """);
    }

    @Override
    protected String entryName() {
        return "Scholarship";
    }

    @Override
    protected String entryDescription() {
        return "Actions, research notes, rewards and manual completion.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
