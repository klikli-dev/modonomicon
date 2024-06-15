// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ConditionRootEntry extends EntryProvider {
    public static final String ID = "condition_root";

    public ConditionRootEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Condition Root");
        this.pageText("""
                Root page for our condition / unlock tests.
                """);
    }

    @Override
    protected String entryName() {
        return "Condition Root Entry";
    }

    @Override
    protected String entryDescription() {
        return "The parent entry for conditional entries.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(1, 0);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.REDSTONE_TORCH);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
