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
import com.klikli_dev.modonomicon.datagen.book.demo.crafting.CraftingIntroEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.crafting.CraftingStickEntry;
import com.klikli_dev.modonomicon.datagen.research.DemoResearch;
import net.minecraft.world.item.Items;

public class CraftingCategory extends CategoryProvider {
    public static final String ID = "crafting";

    public CraftingCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(CraftingIntroEntry.ID).at(0, 0);
        layout.entry(CraftingStickEntry.ID).at(2, 0);
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new CraftingIntroEntry(this).generate());
        this.add(new CraftingStickEntry(this).generate()
                        .withCondition(DemoResearch.CRAFTING_STICK))
                .withParent(introEntry);
    }

    @Override
    protected String categoryName() {
        return "Crafting Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
