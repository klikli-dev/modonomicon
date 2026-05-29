// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryLayout;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AdvancedFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AlwaysLockedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.BasicFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.LinkFormattingEntry;
import net.minecraft.world.item.Items;

public class FormattingCategory extends CategoryProvider {
    public static final String ID = "formatting";

    public FormattingCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(BasicFormattingEntry.ID).at(-8, -1);
        layout.entry(AdvancedFormattingEntry.ID).rightOf(BasicFormattingEntry.ID, 4);
        layout.entry(LinkFormattingEntry.ID).rightOf(AdvancedFormattingEntry.ID, 4).below(1);
        layout.entry(AlwaysLockedEntry.ID).rightOf(LinkFormattingEntry.ID, 6);
    }

    @Override
    protected void generateEntries() {
        var basicFormattingEntry = this.add(new BasicFormattingEntry(this).generate());

        var advancedFormattingEntry = this.add(new AdvancedFormattingEntry(this).generate())
                .withCondition(this.condition().researchNodeUnlocked(this.modLoc("demo/formatting_advanced")))
                .withParent(this.parent(basicFormattingEntry));

        var linkFormattingEntry = this.add(new LinkFormattingEntry(this).generate())
                .withCondition(this.condition().researchNodeUnlocked(this.modLoc("demo/formatting_link")))
                .withParent(advancedFormattingEntry);

        var alwaysLockedEntry = this.add(new AlwaysLockedEntry(this).generate());
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        //When first opening the category, open the basic formatting entry automatically.
        return category.withEntryToOpen(this.modLoc(ID, BasicFormattingEntry.ID), true);
    }

    @Override
    protected String categoryName() {
        return "Formatting Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.BOOK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
