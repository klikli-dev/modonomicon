// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.features;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEmptyPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class EmptyPageEntry extends EntryProvider {
    public static final String ID = "empty";

    public EmptyPageEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Empty Page Entry");
        this.pageText("""
                Empty pages allow to add .. empty pages.
                """);

        this.context().page("empty");
        var empty = BookEmptyPageModel.create();

        this.context().page("empty2");
        var empty2 = BookEmptyPageModel.create();
    }

    @Override
    protected String entryName() {
        return "Empty Page Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing empty pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.OBSIDIAN);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
