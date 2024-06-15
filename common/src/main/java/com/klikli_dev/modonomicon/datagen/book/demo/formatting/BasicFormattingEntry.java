// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.formatting;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class BasicFormattingEntry extends EntryProvider {
    public static final String ID = "basic";

    public BasicFormattingEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle("Basic Formatting");
        // \s tells java to keep the spaces at the end of the line. Otherwise it will remove.
        // Due to markdown using multiple spaces to indicate a line break, we need to keep the spaces.
        this.pageText("""
                **This is bold**    \s 
                *This is italics*    \s
                ++This is underlined++
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                ~~This is stricken through~~   \s
                {0}
                """,
                this.color("Colorful Text!", 0x55FF55)
        );
    }

    @Override
    protected String entryName() {
        return "Basic Formatting Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing entity pages.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
