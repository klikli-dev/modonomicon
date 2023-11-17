package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
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
    protected void generateEntries() {
        var basicFormattingEntry = this.add(this.makeBasicFormattingEntry('b'));
        var advancedFormattingEntry = this.add(this.makeAdvancedFormattingEntry('a'));
        var linkFormattingEntry = this.add(this.makeLinkFormattingEntry('l'));
        var alwaysLockedEntry = this.add(this.makeAlwaysLockedEntry('x'));
        alwaysLockedEntry.withCondition(BookFalseConditionModel.builder().build());

        linkFormattingEntry.withParent(this.parent(advancedFormattingEntry));
        advancedFormattingEntry.withParent(this.parent(basicFormattingEntry));
    }

    @Override
    protected BookCategoryModel generateCategory() {

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon("minecraft:textures/item/book.png");
    }

    private BookEntryModel makeBasicFormattingEntry(char location) {

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

        var formattingEntry = this.entry(location)
                .withIcon("minecraft:textures/item/paper.png")
                .withPage(page1)
                .withPage(page2);

        return formattingEntry;
    }

    private BookEntryModel makeAdvancedFormattingEntry(char location) {
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

        var formattingEntry = this.entry(location)
                .withIcon("minecraft:feather")
                .withEntryBackground(0, 1)
                .withPage(page1)
                .withPage(page2)
                .withPage(page3);

        return formattingEntry;
    }

    private BookEntryModel makeLinkFormattingEntry(char location) {
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

        return this.entry(location)
                .withIcon("minecraft:writable_book")
                .withEntryBackground(0, 2)
                .withPages(page1, page2, page3);
    }

    private BookEntryModel makeAlwaysLockedEntry(char location) {
        this.context().entry("always_locked");

        var entry = this.entry(location)
                .withIcon("minecraft:nether_star")
                .withLocation(this.entryMap().get(location))
                .withEntryBackground(0, 1);

        return entry;
    }
}
