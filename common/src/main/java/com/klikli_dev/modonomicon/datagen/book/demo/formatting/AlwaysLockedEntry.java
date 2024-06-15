// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.formatting;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class AlwaysLockedEntry extends EntryProvider {
    public static final String ID = "always_locked";

    public AlwaysLockedEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {

    }


    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return entry.withCondition(BookFalseConditionModel.create());
    }

    @Override
    protected String entryName() {
        return "Always Locked Entry";
    }

    @Override
    protected String entryDescription() {
        return "Used to demonstrate linking to locked entries";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.CATEGORY_START;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.NETHER_STAR);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
