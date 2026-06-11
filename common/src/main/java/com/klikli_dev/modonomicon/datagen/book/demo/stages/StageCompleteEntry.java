// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.stages;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class StageCompleteEntry extends EntryProvider {
    public static final String ID = "stage_complete";
    private static final Identifier NODE_ID = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo");
    private static final Identifier FINAL_STAGE_ID = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo_stage_3");

    public StageCompleteEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Research Complete");
        this.pageText("""
                All stages have been completed.
                This entry unlocks only after the entire multi-stage research node finishes.
                """);
    }

    @Override
    protected String entryName() {
        return "Stages Complete";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when all research stages are complete.";
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return entry.withCondition(BookResearchStageCompletedConditionModel.create()
                .withNode(NODE_ID)
                .withStage(FINAL_STAGE_ID));
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
