// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DemoRedirectEntry extends EntryProvider {
    public static final String ID = "redirect";

    public DemoRedirectEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        //has no pages, clicking this entry redirects to another category
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return entry.withCategoryToOpen(this.modLoc(IndexModeCategory.ID));
    }

    @Override
    protected String entryName() {
        return "Category Redirect Entry";
    }

    @Override
    protected String entryDescription() {
        return "Redirects to another category.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.LINK_TO_CATEGORY;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ENDER_PEARL);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
