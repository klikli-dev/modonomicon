// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.scholarship;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
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
                Welcome to the scholar's notebook. This entry is the start of the storyline: it teaches you to take and study research notes, and rewards you with insight.
                The Research and Progression categories build on it, with conditions, markers and staged requirements.
                The requirements of the current stage are listed at the bottom of this page.
                """);

        this.stage("observation", s -> s
                .action("observation", "Record your observations",
                        ResearchActionModel.learn(ResearchCostModel.create().item(Items.PAPER)))
                .title("Observation")
                .description("Pay one paper to record your observations.")
                .reward(ResearchEffectModel.experience(10)));

        this.page("observation_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Click to act");
        this.pageText("""
                Some requirements have an action. Click the icon to perform it, the tooltip lists its costs.
                Observations cost one paper and grant experience, which is shown as a reward row once the stage is done.
                """);

        this.page("transcription_take", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Take a blank note");
        this.pageText("""
                The first row of Transcription has an action that hands out a blank research note.
                It costs one paper and wears down your shears. You can take a note even while you carry one.
                The row is ticked once you have taken a note, but the action stays clickable, so you can take more notes later.
                """);

        this.page("transcription_process", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Make a completed note");
        this.pageText("""
                The second row of Transcription is done once you craft a completed research note. Craft the blank note together with an ink sac, as the recipe on the next page shows.
                Vanilla recipes cannot check the marker of an item, so the recipe also accepts plain paper in place of the blank note.
                A real mod would use its own item or a machine for this step.
                """);

        this.page("transcription_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(RESEARCH_NOTE_RECIPE));

        this.page("transcription_submit", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Study");
        this.pageText("""
                The third row of Transcription is done once you use a completed note in the air to study it. The note is consumed, and the rewards are granted once the stage completes.
                Using a blank note does nothing, so the note must be completed first.
                """);

        this.stage("transcription", s -> s
                .fact(DemoResearch.SCHOLARSHIP_NOTE_TAKEN)
                .fact(DemoResearch.SCHOLARSHIP_NOTE_CRAFTED)
                .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
                .title("Transcription")
                .description("Take a blank note, make a completed note from it with an ink sac, then study the completed note in the air.")
                .reward(ResearchEffectModel.addValue(DemoResearch.SCHOLARSHIP_INSIGHT, 1))
                .reward(ResearchEffectModel.grantFact(DemoResearch.SCHOLARSHIP_NOTES_READ)));

        //the study fact of transcription is already granted, so this stage only consumes its cost
        this.stage("synthesis", s -> s
                .consume(Items.IRON_INGOT, 2)
                .title("Synthesis")
                .description("Hold two iron ingots and complete it by hand. The Iron Ingot row counts what you hold, the stage consumes it on completion.")
                .reward(ResearchEffectModel.giveItem(Items.EMERALD))
                .reward(ResearchEffectModel.function(SYNTHESIS_FUNCTION)));

        this.page("synthesis_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Manual completion");
        this.pageText("""
                The last stage is completed by hand with the Complete button of the stage line.
                Its cost, two iron ingots, is listed as an Iron Ingot row that counts the iron you hold, and it is consumed on completion.
                The button is enabled once you hold enough, before that its tooltip says what is missing.
                Completing it grants an emerald and runs a function.
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
