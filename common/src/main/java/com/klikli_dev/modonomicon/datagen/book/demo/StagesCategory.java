// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageCompleteEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageProgressionEntry;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class StagesCategory extends CategoryProvider {
    public static final String ID = "stages";
    private static final Identifier STAGES_NODE_ID = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo");
    private static final Identifier FINAL_STAGE_ID = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "demo/stages_demo_stage_3");

    public StagesCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(StageProgressionEntry.ID).at(0, 0);
        layout.entry(StageCompleteEntry.ID).at(0, 2);
    }

    @Override
    protected void generateEntries() {
        var progression = this.add(new StageProgressionEntry(this).generate());
        this.add(new StageCompleteEntry(this).generate()
                .withCondition(BookResearchStageCompletedConditionModel.create()
                        .withNode(STAGES_NODE_ID)
                        .withStage(FINAL_STAGE_ID))
                .withParent(progression));
    }

    @Override
    protected String categoryName() {
        return "Research Stages Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.END_CRYSTAL);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
