// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.Demo1IndexEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.indexmode.Demo2IndexEntry;
import net.minecraft.world.item.Items;

public class IndexModeCategory extends CategoryProvider {
    public static final String ID = "index_mode";

    public IndexModeCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        //Index mode categories don't need an entry map.
        //However, make sure not to query it either, otherwise you will get an unhappy surprise!
        return new String[0];
    }

    @Override
    protected void generateEntries() {
        this.add(new Demo1IndexEntry(this).generate());
        this.add(new Demo2IndexEntry(this).generate());
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        //This makes this one category display in index mode.
        //If you want the whole book in index mode, do the same thing in the (single)book provider
        return category.withDisplayMode(BookDisplayMode.INDEX);
    }

    @Override
    protected String categoryName() {
        return "Index Mode Category";
    }

    @Override
    protected String categoryDescription() {
        return "A category showcasing how Modonomicon works in index mode."; //This is currently only shown for index-mode books
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String categoryId() {
        return ID;
    }
}
