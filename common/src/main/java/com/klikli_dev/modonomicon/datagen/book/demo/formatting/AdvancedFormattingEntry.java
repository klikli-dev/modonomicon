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

public class AdvancedFormattingEntry extends EntryProvider {
    public static final String ID = "advanced";

    public AdvancedFormattingEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {

        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Advanced Formatting");
        this.pageText("""
                <t>this.could.be.a.translation.key<t>    \s
                ***This is bold italics***    \s
                *++This is italics underlined++*
                {0}
                {1}
                """,
                this.itemLink(Items.DIAMOND),
                this.itemLink("TestText", Items.EMERALD)
        );

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                Unordered List:
                - List item 
                - List item 2
                - List item 3

                Ordered List:
                1. Entry 1
                2. Entry 2
                """
        );

        this.page("page3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Ridiculously superlong title that should be scaled!");
        this.pageText("""
                This page is to test title scaling!
                """
        );
    }

    @Override
    protected String entryName() {
        return "Advanced Formatting Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing advanced formatting.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.CATEGORY_START;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
