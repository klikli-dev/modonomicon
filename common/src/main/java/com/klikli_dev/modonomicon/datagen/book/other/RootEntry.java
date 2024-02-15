// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.other;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class RootEntry extends EntryProvider {
    public RootEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () ->
                BookTextPageModel.builder()
                        .withText(this.context().pageText())
                        .withTitle(this.context().pageTitle())
                        .build()
        );
        this.pageTitle("Entry Root");
        this.pageText("Root");
    }

    @Override
    protected String entryName() {
        return "Root Entry";
    }

    @Override
    protected String entryDescription() {
        return "Used for testing some condition stuff.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SUGAR);
    }

    @Override
    protected String entryId() {
        return "root";
    }
}
