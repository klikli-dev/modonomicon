/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class DemoBookProvider extends BookProvider {

    public DemoBookProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modid, ModonomiconLanguageProvider lang, ModonomiconLanguageProvider... translations) {
        super("demo", packOutput,  registries, modid, lang, translations);
    }

    @Override
    protected BookModel generateBook() {
        this.add(this.context().bookName(), "Demo Book");
        this.add(this.context().bookTooltip(), "A book to showcase & test Modonomicon features.");

        //if we want to handle a second language in here we can access it like this:
//        this.add(this.lang("ru_ru"), this.context().bookName(), "Демонстрационная книга");
//        this.add(lang("ru_ru"), this.context().bookTooltip(), "Книга для демонстрации и тестирования функций \"Модономикона\".");

        var commandEntryCommand = BookCommandModel.create(this.modLoc("test_command"), "/give @s minecraft:apple 1")
                .withPermissionLevel(2)
                .withSuccessMessage("modonomicon.command.test_command.success");
        this.add(commandEntryCommand.getSuccessMessage(), "You got an apple, because reading is cool!");
        var commandEntryLinkCommand = BookCommandModel.create(this.modLoc("test_command2"), "/give @s minecraft:wheat 1")
                .withPermissionLevel(2)
                .withSuccessMessage("modonomicon.command.test_command2.success");
        this.add(commandEntryLinkCommand.getSuccessMessage(), "You got wheat, because clicking is cool!");

        return BookModel.create(this.modLoc("demo"), this.context().bookName())
                .withTooltip(this.context().bookTooltip())
                .withModel(ResourceLocation.parse("modonomicon:modonomicon_green"))
                .withBookTextOffsetX(5)
                .withBookTextOffsetY(0) //no top offset
                .withBookTextOffsetWidth(-5)
                .withCommand(commandEntryCommand)
                .withCommand(commandEntryLinkCommand);
    }

    @Override
    protected void generateCategories() {
        //for the two big categories we use the category provider
        var featuresCategory = this.add(new FeaturesCategoryProvider(this).generate());
        var formattingCategory = this.add(new FormattingCategoryProvider(this).generate());

        var hiddenCategory = this.add(this.makeHiddenCategory(this.context()));
        var conditionalCategory = this.add(this.makeConditionalCategory(this.context()));
        conditionalCategory.withCondition(BookEntryReadConditionModel.create().withEntry(this.modLoc("features/condition_root")));

        var otherCategory = this.add(new OtherCategoryProvider(this).generate());

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
                .withIcon(Items.NETHER_STAR)
                .withLocation(entryHelper.get('l'))
                .withEntryBackground(0, 1)
                .withCondition(BookFalseConditionModel.create());

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon(Items.BOOK)
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
                .withIcon(Items.NETHER_STAR)
                .withLocation(entryHelper.get('l'))
                .withEntryBackground(0, 1)
                .withCondition(BookFalseConditionModel.create());

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon(Blocks.CHEST)
                .withEntry(entry);
    }
}
