// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

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
    protected void generateEntries() {
        var basicFormattingEntry = this.add(this.makeBasicFormattingEntry('b'));
        var advancedFormattingEntry = this.add(this.makeAdvancedFormattingEntry('a'));
        var linkFormattingEntry = this.add(this.makeLinkFormattingEntry('l'));
        var alwaysLockedEntry = this.add(this.makeAlwaysLockedEntry('x'));
        alwaysLockedEntry.withCondition(BookFalseConditionModel.create());

        linkFormattingEntry.withParent(this.parent(advancedFormattingEntry));
        advancedFormattingEntry.withParent(this.parent(basicFormattingEntry));
    }

    @Override
    protected BookCategoryModel generateCategory() {

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon(ResourceLocation.parse("minecraft:textures/item/book.png"));
    }

    private BookEntryModel makeBasicFormattingEntry(char location) {

        this.context().entry("basic");
        this.context().page("page1");
        var page1 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //bold, italics, underlines,

        this.context().page("page2");
        var page2 = BookTextPageModel.create()
                .withText(this.context().pageText())
                //.withTitle(context().pageTitle())
                ; //strikethrough, color

        var formattingEntry = this.entry(location)
                .withIcon(ResourceLocation.parse("minecraft:textures/item/paper.png"))
                .withPage(page1)
                .withPage(page2);

        return formattingEntry;
    }

    private BookEntryModel makeAdvancedFormattingEntry(char location) {
        this.context().entry("advanced");
        this.context().page("page1");
        var page1 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //translatable texts, mixed formatting

        this.context().page("page2");
        var page2 = BookTextPageModel.create()
                .withText(this.context().pageText())
                //.withTitle(context().pageTitle())
                ; //lists

        this.context().page("page3");
        var page3 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //lists

        var formattingEntry = this.entry(location)
                .withIcon(Items.FEATHER)
                .withEntryBackground(0, 1)
                .withPage(page1)
                .withPage(page2)
                .withPage(page3);

        return formattingEntry;
    }

    private BookEntryModel makeLinkFormattingEntry(char location) {
        this.context().entry("link");
        this.context().page("page1");
        var page1 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //http links

        this.context().page("page2");
        var page2 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //book entry links

        this.context().page("page3");
        var page3 = BookTextPageModel.create()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle()); //patchouli link

        return this.entry(location)
                .withIcon(Items.WRITABLE_BOOK)
                .withEntryBackground(0, 2)
                .withPages(page1, page2, page3);
    }

    private BookEntryModel makeAlwaysLockedEntry(char location) {
        this.context().entry("always_locked");

        var entry = this.entry(location)
                .withIcon(Items.NETHER_STAR)
                .withLocation(this.entryMap().get(location))
                .withEntryBackground(0, 1);

        return entry;
    }
}
