// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.scholarship.ScholarshipEntry;
import net.minecraft.world.item.Items;

/**
 * The first category of the demo book and its entry point. It is not gated by any condition, so the storyline is visible from the start.
 */
public class ScholarshipCategory extends CategoryProvider {
    public static final String ID = "scholarship";

    public ScholarshipCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void generateEntries() {
        var scholarship = this.add(new ScholarshipEntry(this).generate());
        this.layout().entry(scholarship).at(0, 0);
    }

    @Override
    protected String categoryName() {
        return "Scholarship";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
