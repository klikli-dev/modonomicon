// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.progression;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchActionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCostModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchRewardModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * A staged progression demonstrating research actions, the take, process and submit loop of an item_used fact, rewards and manual completion.
 */
public class ScholarshipEntry extends EntryProvider {
    public static final String ID = "scholarship";
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
                This entry is a research progression with three stages, each one teaching a mechanic.
                The requirements of the current stage are listed at the bottom of this page.
                """);

        this.stage("observation", s -> s
                .action("observation", "Record your observations",
                        ResearchActionModel.grantFact(ResearchCostModel.create().item(Items.PAPER)))
                .title("Observation")
                .description("Pay one paper to record your observations.")
                .reward(ResearchRewardModel.experience(10)));

        this.page("observation_done", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Click to act");
        this.pageText("""
                Some requirements have an action. Click the icon to perform it, the tooltip lists its costs.
                Observations cost one paper and grant experience, which is shown as a reward row once the stage is done.
                """);

        this.stage("transcription", s -> s
                .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
                .title("Transcription")
                .description("Take a blank note, process it into a completed note, then use the completed note to study it.")
                .reward(ResearchRewardModel.addValue(DemoResearch.SCHOLARSHIP_INSIGHT, 1))
                .reward(ResearchRewardModel.grantFact(DemoResearch.SCHOLARSHIP_NOTES_READ)));

        this.page("transcription_take", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Take");
        this.pageText("""
                The requirement row of Transcription has an action that hands out a blank research note.
                It costs one paper and wears down your shears. You can take a note even while you carry one.
                It is repeatable: it stays clickable after the stage is done, so you can take more notes later to study them again.
                """);

        this.page("transcription_process", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Process");
        this.pageText("""
                Craft the blank note together with an ink sac into a completed research note, as the recipe on the next page shows.
                Vanilla recipes cannot check the marker of an item, so plain paper works in the recipe too.
                A real mod would use its own item or a machine for this step.
                """);

        this.page("transcription_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(RESEARCH_NOTE_RECIPE));

        this.page("transcription_submit", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Submit");
        this.pageText("""
                Use the completed note in the air to study it. The note is consumed, the row is ticked and the rewards are granted once the stage completes.
                Using the blank note does nothing, so the note must be completed first.
                """);

        this.stage("synthesis", s -> s
                .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
                .consume(Items.IRON_INGOT, 2)
                .title("Synthesis")
                .description("Hold two iron ingots and complete it by hand. The Iron Ingot row counts what you hold, the stage consumes it on completion.")
                .reward(ResearchRewardModel.item(Items.EMERALD))
                .reward(ResearchRewardModel.function(SYNTHESIS_FUNCTION)));

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
