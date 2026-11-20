/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FormattingCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.ScholarshipCategory;
import net.minecraft.resources.Identifier;

public class DemoBook extends SingleBookSubProvider {

    public static final String ID = "demo";

    public DemoBook() {
        super(ID, Modonomicon.MOD_ID);
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        //if we want to handle a second language in here we can add book-related (not category or entry or page) translations here
        //this.add(this.lang("ru_ru"), this.context().bookName(), "Ð”ÐµÐ¼Ð¾Ð½ÑÑ‚Ñ€Ð°Ñ†Ð¸Ð¾Ð½Ð½Ð°Ñ ÐºÐ½Ð¸Ð³Ð°");
        //this.add(lang("ru_ru"), this.context().bookTooltip(), "ÐšÐ½Ð¸Ð³Ð° Ð´Ð»Ñ Ð´ÐµÐ¼Ð¾Ð½ÑÑ‚Ñ€Ð°Ñ†Ð¸Ð¸ Ð¸ Ñ‚ÐµÑÑ‚Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ Ñ„ÑƒÐ½ÐºÑ†Ð¸Ð¹ \"ÐœÐ¾Ð´Ð¾Ð½Ð¾Ð¼Ð¸ÐºÐ¾Ð½Ð°\".");

        var commandEntryCommand = BookCommandModel.create(this.modLoc("test_command"), "/give @s minecraft:apple 1")
                .withSuccessMessage("modonomicon.command.test_command.success");
        this.add(commandEntryCommand.getSuccessMessage(), "You got an apple, because reading is cool!");

        var commandEntryLinkCommand = BookCommandModel.create(this.modLoc("test_command2"), "/give @s minecraft:wheat 1")
                .withSuccessMessage("modonomicon.command.test_command2.success")
                .withAllowedEntry("modonomicon:features/command");
        this.add(commandEntryLinkCommand.getSuccessMessage(), "You got wheat, because clicking is cool!");

        return book.withModel(Identifier.parse("modonomicon:modonomicon_green"))
                .withTheme(theme -> theme.withLayout(layout -> layout
                        .withBookTextOffsetX(5)
                        .withBookTextOffsetY(0)
                        .withBookTextOffsetWidth(-5)))
                .withCommand(commandEntryCommand)
                .withCommand(commandEntryLinkCommand)
                .withAllowOpenBooksWithInvalidLinks(true)
                .withGenerateEntryHierarchyResearch(true);
    }

    @Override
    protected void registerDefaultMacros() {
        //currently no macros
    }

    @Override
    protected void generateCategories() {
        //for the two big categories we use the category provider, the sort numbers keep the tab order explicit
        var featuresCategory = this.add(new FeaturesCategory(this).generate());

        //the scholarship category is the storyline of the book, it is not gated, so it is shown right after features
        var scholarshipCategory = this.add(new ScholarshipCategory(this).generate().withSortNumber(2));

        var formattingCategory = this.add(new FormattingCategory(this).generate().withSortNumber(3));

        var indexModeCategory = this.add(new IndexModeCategory(this).generate().withSortNumber(4));
    }

    @Override
    protected String bookName() {
        return "Demo Book";
    }

    @Override
    protected String bookTooltip() {
        return "A book to showcase & test Modonomicon features.";
    }
}

