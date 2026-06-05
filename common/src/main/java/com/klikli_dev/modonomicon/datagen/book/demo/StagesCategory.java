// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageCompleteEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.stages.StageProgressionEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class StagesCategory extends CategoryProvider {
    public static final String ID = "stages";

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
                .withCondition(DemoResearch.STAGES_DEMO)
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
