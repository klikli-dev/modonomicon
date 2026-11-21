// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.scholarship;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

/**
 * The sources shelf. Viewing it once increments the sources consulted value, which the catalogue requires.
 */
public class SourcesEntry extends EntryProvider {
    public static final String ID = "sources";

    public SourcesEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sources shelf");
        this.pageText("""
                Ungated value: viewing this entry once adds 1 to sources consulted.
                """);
    }

    @Override
    protected String entryName() {
        return "Sources Shelf";
    }

    @Override
    protected String entryDescription() {
        return "Ungated value collector: viewing it counts a source.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BOOKSHELF);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
