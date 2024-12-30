// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.demo.formatting;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FormattingCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionLevel1Entry;
import com.klikli_dev.modonomicon.datagen.book.demo.features.MultiblockEntry;
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

        this.page("page4", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Scaled Page");
        this.pageText("""
                        This page features a very long text that will be scaled down to fit the page size.
                        {0} to check if click detection works on scaled texts. It has {1} for the same reason.
                        Finally it has a {2} because why not. From now on we just add some text with no particular function except to make the page longer. 
                        Not extremely useful for the reader, but necessary for the poor Kli-Kli testing this.
                        """,
                this.entryLink("It has a Link", FeaturesCategory.ID, MultiblockEntry.ID),
                this.entryLink("another Link", FormattingCategory.ID, BasicFormattingEntry.ID),
                this.entryLink("third link", FormattingCategory.ID, AlwaysLockedEntry.ID)
        );

        this.page("list_test", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        //the following pattern can be used to add one newline after a list.
        //it forces the md parser to exit the list context and add an empty line
        //additional "\\" after the first will add more newlines. Most likely the "\\" must be on a new line each.
        this.pageText("""
                Unordered List:
                - List item 
                - List item 2
                - List item 3
                
                \\
                And now some other text.
                """
        );

        this.page("dynamic_macro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Dynamic macro");
        this.pageText("""
                This text comes from a macro (Random.nextDouble()): [{}](my.test.macro)
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
