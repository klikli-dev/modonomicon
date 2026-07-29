/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.acquiring.AcquiringCobblestoneEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.acquiring.AcquiringIntroEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class AcquiringCategory extends CategoryProvider {
    public static final String ID = "acquiring";

    public AcquiringCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(AcquiringIntroEntry.ID).at(0, 0);
        layout.entry(AcquiringCobblestoneEntry.ID).at(2, 0);
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new AcquiringIntroEntry(this).generate());
        this.add(new AcquiringCobblestoneEntry(this).generate()
                        .withCondition(DemoResearch.ACQUIRE_COBBLESTONE))
                .withParent(introEntry);
    }

    @Override
    protected String categoryName() {
        return "Acquiring Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.CHEST);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
