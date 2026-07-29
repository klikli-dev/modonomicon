// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.stages;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class StageProgressionEntry extends EntryProvider {
    public static final String ID = "stage_progression";
    private static final Identifier NODE_ID = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo");

    public StageProgressionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        // Page 1: always visible
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Research Progression");
        this.pageText("""
                This entry demonstrates research stages.
                Each page unlocks as you complete stages by crafting oak planks.
                Craft 1 oak plank to unlock the next page.
                """);

        // Page 2: gated by stage 1 (1 plank crafted)
        this.page("stage_1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(NODE_ID)
                        .withStage(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo_stage_1"))));
        this.pageTitle("Stage 1 Complete");
        this.pageText("""
                You crafted your first oak plank and completed stage 1.
                Craft 2 more (3 total) to unlock the next stage.
                """);

        // Page 3: gated by stage 2 (3 planks crafted)
        this.page("stage_2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(NODE_ID)
                        .withStage(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo_stage_2"))));
        this.pageTitle("Stage 2 Complete");
        this.pageText("""
                Stage 2 is done. You have crafted 3 oak planks.
                Craft 2 more (5 total) to reach the final stage.
                """);

        // Page 4: gated by stage 3 (5 planks crafted)
        this.page("stage_3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(NODE_ID)
                        .withStage(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo_stage_3"))));
        this.pageTitle("All Stages Complete");
        this.pageText("""
                All research stages have been completed.
                The next entry in this category is now unlocked.
                """);
    }

    @Override
    protected String entryName() {
        return "Stage Progression";
    }

    @Override
    protected String entryDescription() {
        return "Craft oak planks to unlock each page.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.OAK_PLANKS);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
