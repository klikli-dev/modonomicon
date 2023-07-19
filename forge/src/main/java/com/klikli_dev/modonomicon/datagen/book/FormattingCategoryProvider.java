/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

public class FormattingCategoryProvider extends CategoryProvider {
    public FormattingCategoryProvider(BookProvider parent) {
        super(parent, "formatting");
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "__b___a______________",
                "__________l_____x____",
                "_____________________",
                "_____________________"
        };
    }

    @Override
    protected BookCategoryModel generateCategory() {
        var basicFormattingEntry = this.makeBasicFormattingEntry('b');
        var advancedFormattingEntry = this.makeAdvancedFormattingEntry('a');
        var linkFormattingEntry = this.makeLinkFormattingEntry('l');
        var alwaysLockedEntry = this.makeAlwaysLockedEntry('x');
        alwaysLockedEntry.withCondition(BookFalseConditionModel.builder().build());

        linkFormattingEntry.withParent(BookEntryParentModel.builder().withEntryId(advancedFormattingEntry.id).build());
        advancedFormattingEntry.withParent(BookEntryParentModel.builder().withEntryId(basicFormattingEntry.id).build());

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon("minecraft:textures/item/book.png")
                .withEntries(
                        basicFormattingEntry.build(),
                        advancedFormattingEntry.build(),
                        linkFormattingEntry.build(),
                        alwaysLockedEntry.build()
                );
    }

    private BookEntryModel.Builder makeBasicFormattingEntry(char location) {

        this.context().entry("basic");
        this.context().page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //bold, italics, underlines,

        this.context().page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                //.withTitle(context().pageTitle())
                .build(); //strikethrough, color

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:textures/item/paper.png")
                .withLocation(this.entryMap().get(location))
                .withPage(page1)
                .withPage(page2);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeAdvancedFormattingEntry(char location) {
        this.context().entry("advanced");
        this.context().page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //translatable texts, mixed formatting

        this.context().page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                //.withTitle(context().pageTitle())
                .build(); //lists

        this.context().page("page3");
        var page3 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //lists

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:feather")
                .withLocation(this.entryMap().get(location))
                .withEntryBackground(0, 1)
                .withPage(page1)
                .withPage(page2)
                .withPage(page3);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeLinkFormattingEntry(char location) {
        this.context().entry("link");
        this.context().page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //http links

        this.context().page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //book entry links

        this.context().page("page3");
        var page3 = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build(); //patchouli link

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc("formatting/link"))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:writable_book")
                .withLocation(this.entryMap().get(location))
                .withEntryBackground(0, 2)
                .withPages(page1, page2, page3);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeAlwaysLockedEntry(char location) {
        this.context().entry("always_locked");

        var entry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:nether_star")
                .withLocation(this.entryMap().get(location))
                .withEntryBackground(0, 1);

        return entry;
    }
}
