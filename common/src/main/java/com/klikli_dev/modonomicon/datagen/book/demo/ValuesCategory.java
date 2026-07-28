// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorAEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorBEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.values.CollectorCompleteEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class ValuesCategory extends CategoryProvider {
    public static final String ID = "values";

    public ValuesCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(CollectorAEntry.ID).at(0, 0);
        layout.entry(CollectorBEntry.ID).at(1, 0);
        layout.entry(CollectorCEntry.ID).at(2, 0);
        layout.entry(CollectorCompleteEntry.ID).at(1, 1);
    }

    @Override
    protected void generateEntries() {
        this.add(new CollectorAEntry(this).generate());
        this.add(new CollectorBEntry(this).generate());
        this.add(new CollectorCEntry(this).generate());
        this.add(new CollectorCompleteEntry(this).generate()
                .withCondition(DemoResearch.COLLECTOR_COMPLETE));
    }

    @Override
    protected String categoryName() {
        return "Values Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
