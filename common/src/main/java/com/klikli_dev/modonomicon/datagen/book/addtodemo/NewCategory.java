// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.addtodemo;

import com.klikli_dev.modonomicon.api.datagen.AddToBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.addtodemo.newcat.IntroEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class NewCategory extends CategoryProvider {
    public static final String ID = "new";

    public NewCategory(AddToBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        //The entry map allows to define where entries are in relation to each other.
        //It is recommended to use a single character per entry
        // (if you think you are running out of characters .. any unicode character works.)
        //Alternatively the format used for multiblock below can be used.
        return new String[]{
                "_____________________",
                "_____________________",
                "_____________________",
                "__________i__________",
                "_____________________",
                "_____________________",
                "_____________________"
        };
    }

    @Override
    protected void generateEntries() {
        this.add(new IntroEntry(this).generate('i'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category).withBackground(ResourceLocation.fromNamespaceAndPath("theurgy", "textures/gui/book/bg_nightsky.png"));
    }

    @Override
    protected String categoryName() {
        return "Features Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.NETHER_STAR);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
