/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

public class DemoBookProvider extends BookProvider {

    public DemoBookProvider(PackOutput packOutput, String modid, LanguageProvider lang, LanguageProvider... translations) {
        super("demo", packOutput, modid, lang, translations);
    }

    @Override
    protected BookModel generateBook() {
        this.lang().add(this.context().bookName(), "Demo Book");
        this.lang().add(this.context().bookTooltip(), "A book to showcase & test Modonomicon features.");

        this.lang("ru_ru").add(this.context().bookName(), "Демонстрационная книга");
        this.lang("ru_ru").add(this.context().bookTooltip(), "Книга для демонстрации и тестирования функций \"Модономикона\".");

        //for the two big categories we use the category provider
        var featuresCategory = new FeaturesCategoryProvider(this).generate();
        var formattingCategory = new FormattingCategoryProvider(this).generate();

        var hiddenCategory = this.makeHiddenCategory(this.context());
        var conditionalCategory = this.makeConditionalCategory(this.context());
        conditionalCategory.withCondition(BookEntryReadConditionModel.builder().withEntry(this.modLoc("features/condition_root")).build());


        var commandEntryCommand = BookCommandModel.create(this.modLoc("test_command"), "/give @s minecraft:apple 1")
                .withPermissionLevel(2)
                .withSuccessMessage("modonomicon.command.test_command.success");
        this.lang.add(commandEntryCommand.getSuccessMessage(), "You got an apple, because reading is cool!");
        var commandEntryLinkCommand = BookCommandModel.create(this.modLoc("test_command2"), "/give @s minecraft:wheat 1")
                .withPermissionLevel(2)
                .withSuccessMessage("modonomicon.command.test_command2.success");
        this.lang.add(commandEntryLinkCommand.getSuccessMessage(), "You got wheat, because clicking is cool!");

        var demoBook = BookModel.create(this.modLoc("demo"), this.context().bookName())
                .withTooltip(this.context().bookTooltip())
                .withModel(new ResourceLocation("modonomicon:modonomicon_green"))
                .withBookTextOffsetX(5)
                .withBookTextOffsetY(0) //no top offset
                .withBookTextOffsetWidth(-5)
                .withCategory(featuresCategory)
                .withCategory(formattingCategory)
                .withCategory(hiddenCategory)
                .withCategory(conditionalCategory)
                .withCommand(commandEntryCommand)
                .withCommand(commandEntryLinkCommand);
        return demoBook;
    }

    @Override
    protected void registerDefaultMacros() {
        //none currently
    }

    private BookCategoryModel makeHiddenCategory(BookContextHelper helper) {
        this.context().category("hidden");

        var entryHelper = new CategoryEntryMap();
        entryHelper.setMap(
                "_____________________",
                "_____________________",
                "__________l__________",
                "_____________________",
                "_____________________"
        );

        this.context().entry("always_locked");

        var entry = BookEntryModel.create(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()), this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:nether_star")
                .withLocation(entryHelper.get('l'))
                .withEntryBackground(0, 1)
                .withCondition(BookFalseConditionModel.builder().build());

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon("minecraft:book")
                .withShowCategoryButton(false)
                .withEntry(entry);
    }

    private BookCategoryModel makeConditionalCategory(BookContextHelper helper) {
        this.context().category("conditional");

        var entryHelper = new CategoryEntryMap();
        entryHelper.setMap(
                "_____________________",
                "_____________________",
                "__________l__________",
                "_____________________",
                "_____________________"
        );

        this.context().entry("always_locked");

        var entry = BookEntryModel.create(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()), this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:nether_star")
                .withLocation(entryHelper.get('l'))
                .withEntryBackground(0, 1)
                .withCondition(BookFalseConditionModel.builder().build());

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon("minecraft:chest")
                .withEntry(entry);
    }
}
