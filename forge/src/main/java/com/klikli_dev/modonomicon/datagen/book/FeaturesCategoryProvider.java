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
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCategoryProvider extends CategoryProvider {
    public FeaturesCategoryProvider(BookProvider parent) {
        super(parent, "features");
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "___           _____5____________",
                "__(multiblock)______________d___",
                "___           _______r__________",
                "__c           __________________",
                "___           _______2___3___i__",
                "__s           _____e____________",
                "___           __________________",
                "___           _____f____________"
        };
    }

    @Override
    protected BookCategoryModel generateCategory() {
        var multiblockEntry = this.makeMultiblockEntry("multiblock");

        var conditionEntries = this.makeConditionEntries('r', '1', '2');

        var recipeEntry = this.makeRecipeEntry('c');

        var spotlightEntry = this.makeSpotlightEntry('s');
        spotlightEntry.withParent(BookEntryParentModel.builder().withEntryId(recipeEntry.getId()).build());

        var emptyEntry = this.makeEmptyPageEntry('e');
        emptyEntry.withParent(BookEntryParentModel.builder().withEntryId(spotlightEntry.id).build());

        var commandEntry = this.makeCommandEntry('f');
        commandEntry.withParent(emptyEntry);
        commandEntry.withCommandToRunOnFirstRead(this.modLoc("test_command"));


        var entityEntry = this.makeEntityEntry('d');

        var imageEntry = this.makeImageEntry('i');
        imageEntry.withParent(BookEntryParentModel.builder().withEntryId(emptyEntry.id).build());

        var redirectEntry = this.makeRedirectEntry('5');

        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon("minecraft:nether_star")
                .withBackgroundParallaxLayers(
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/base.png"), 0.7f, -1),
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/1.png"), 1f, -1),
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/2.png"), 1.4f, -1)
                )
                .withEntries(conditionEntries.stream().map(e -> e.build()).toArray(BookEntryModel[]::new))
                .withEntries(
                        multiblockEntry.build(),
                        recipeEntry.build(),
                        spotlightEntry.build(),
                        emptyEntry.build(),
                        entityEntry.build(),
                        imageEntry.build(),
                        redirectEntry.build(),
                        commandEntry.build()
                );
    }

    private BookEntryModel.Builder makeMultiblockEntry(String location) {
        this.context().entry("multiblock");

        this.context().page("intro");
        var multiBlockIntroPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("preview");
        var multiblockPreviewPage = BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("blockentity"))
                .withMultiblockName("multiblocks.modonomicon.blockentity")
                .withText(this.context().pageText())
                .build();

        this.context().page("preview2");
        var multiblockPreviewPage2 = BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("tag"))
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:furnace")
                .withLocation(this.entryMap().get(location))
                .withPages(multiBlockIntroPage, multiblockPreviewPage, multiblockPreviewPage2);
    }

    private List<BookEntryModel.Builder> makeConditionEntries(char rootLocation, char level1Location, char level2Location) {
        var result = new ArrayList<BookEntryModel.Builder>();

        this.context().entry("condition_root");
        this.context().page("info");
        var conditionRootEntryInfoPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        var conditionRootEntry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:redstone_torch")
                .withLocation(this.entryMap().get(rootLocation))
                .withEntryBackground(1, 0)
                .withPages(conditionRootEntryInfoPage);
        result.add(conditionRootEntry);

        this.context().entry("condition_level_1");
        this.context().page("info");
        var conditionLevel1EntryInfoPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        var conditionLevel1EntryCondition = BookEntryReadConditionModel.builder()
                .withEntry(conditionRootEntry.getId())
                .build();
        var conditionLevel1Entry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:lever")
                .withLocation(this.entryMap().get(level1Location))
                .withPages(conditionLevel1EntryInfoPage)
                .withCondition(conditionLevel1EntryCondition)
                .withParent(BookEntryParentModel.builder().withEntryId(conditionRootEntry.getId()).build());
        result.add(conditionLevel1Entry);

        this.context().entry("condition_level_2");
        this.context().page("info");
        var conditionLevel2EntryInfoPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        var conditionLevel2EntryCondition = BookEntryUnlockedConditionModel.builder()
                .withEntry(conditionLevel1Entry.getId())
                .build();
        var conditionLevel2Entry = BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:torch")
                .withLocation(this.entryMap().get(level2Location))
                .withPages(conditionLevel2EntryInfoPage)
                .withCondition(conditionLevel2EntryCondition)
                .withParent(BookEntryParentModel.builder().withEntryId(conditionLevel1Entry.getId()).build());
        result.add(conditionLevel2Entry);

        return result;
    }

    private BookEntryModel.Builder makeRecipeEntry(char location) {
        this.context().entry("recipe");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("crafting");
        var crafting = BookCraftingRecipePageModel.builder()
                .withRecipeId1("minecraft:crafting_table")
                .withRecipeId2("minecraft:oak_planks")
                .withText(this.context().pageText())
                .withTitle2("test.test.test")
                .build();

        this.context().page("smelting");
        var smelting = BookSmeltingRecipePageModel.builder()
                .withRecipeId1("minecraft:charcoal")
                .withRecipeId2("minecraft:cooked_beef")
                .build();

        this.context().page("smoking");
        var smoking = BookSmokingRecipePageModel.builder()
                .withRecipeId1("minecraft:cooked_beef_from_smoking")
                .withText(this.context().pageText())
                .build();

        this.context().page("blasting");
        var blasting = BookBlastingRecipePageModel.builder()
                .withRecipeId2("minecraft:iron_ingot_from_blasting_iron_ore")
                .build();

        this.context().page("campfire_cooking");
        var campfireCooking = BookCampfireCookingRecipePageModel.builder()
                .withRecipeId1("minecraft:cooked_beef_from_campfire_cooking")
                .build();

        this.context().page("stonecutting");
        var stonecutting = BookStonecuttingRecipePageModel.builder()
                .withRecipeId1("minecraft:andesite_slab_from_andesite_stonecutting")
                .build();

        this.context().page("smithing");
        var smithing = BookSmithingRecipePageModel.builder()
                .withRecipeId1("minecraft:netherite_axe_smithing")
                .withTitle1(this.context().pageTitle())
                .withRecipeId2("minecraft:netherite_chestplate_smithing")
                .build();
        this.add(this.context().pageTitle(), "1.20+ Smithing Recipe");


        this.context().page("missing");
        var missing = BookSmithingRecipePageModel.builder()
                .withRecipeId1("minecraft:netherite_axe_smithing_does_not_exist")
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:crafting_table")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, crafting, smelting, smoking, blasting, campfireCooking, stonecutting, smithing, missing);
    }

    private BookEntryModel.Builder makeSpotlightEntry(char location) {
        this.context().entry("spotlight");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("spotlight1");
        var spotlight1 = BookSpotlightPageModel.builder()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withItem(Ingredient.of(Items.APPLE))
                .build();

        this.context().page("spotlight2");
        var spotlight2 = BookSpotlightPageModel.builder()
                .withText(this.context().pageText())
                .withItem(Ingredient.of(Items.DIAMOND))
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:beacon")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, spotlight1, spotlight2);
    }

    private BookEntryModel.Builder makeEmptyPageEntry(char location) {
        this.context().entry("empty");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("empty");
        var empty = BookEmptyPageModel.builder()
                .build();

        this.context().page("empty2");
        var empty2 = BookEmptyPageModel.builder()
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:obsidian")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, empty, empty2);
    }

    private BookEntryModel.Builder makeCommandEntry(char location) {
        this.context().entry("command");

        this.add(this.context().entryName(), "Entry Read Commands");
        this.add(this.context().entryDescription(), "This entry runs a command when you first read it.");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.add(this.context().pageTitle(), "Entry Read Commands");
        this.add(this.context().pageText(), "This entry just ran a command when you first read it. Look into your chat!");

        this.context().page("command_link");
        var commandLink = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.add(this.context().pageTitle(), "Command Link");
        this.add(this.context().pageText(), "{0}", this.commandLink("Click me to run the command!", "test_command2"));

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:oak_sign")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, commandLink);
    }


    private BookEntryModel.Builder makeEntityEntry(char location) {
        this.context().entry("entity");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("entity1");
        var entity1 = BookEntityPageModel.builder()
                .withEntityName(this.context().pageTitle())
                .withEntityId("minecraft:ender_dragon")
                .withScale(0.5f)
                .build();

        this.context().page("entity2");
        var entity2 = BookEntityPageModel.builder()
                .withText(this.context().pageText())
                .withEntityId("minecraft:spider")
                .withScale(1f)
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:spider_eye")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, entity1, entity2);
    }

    private BookEntryModel.Builder makeImageEntry(char location) {
        this.context().entry("image");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.context().page("image");
        var imagePage = BookImagePageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .withImages(
                        new ResourceLocation("modonomicon:textures/gui/default_background.png"),
                        new ResourceLocation("modonomicon:textures/gui/dark_slate_seamless.png")
                )
                .build();

        //now test if  tooltips render correctly over the image
        this.context().page("test_spotlight");
        var testSpotlight = BookSpotlightPageModel.builder()
                .withText(this.context().pageText())
                .withItem(Ingredient.of(Blocks.SPAWNER))
                .build();

        this.context().page("test_image");
        var testImage = BookImagePageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .withImages(
                        new ResourceLocation("modonomicon:textures/gui/default_background.png"),
                        new ResourceLocation("modonomicon:textures/gui/dark_slate_seamless.png")
                )
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:item_frame")
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, imagePage, testSpotlight, testImage);
    }

    private BookEntryModel.Builder makeRedirectEntry(char location) {
        this.context().entry("redirect");

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().categoryId() + "/" + this.context().entryId()))
                .withName(this.context().entryName())
                .withDescription(this.context().entryDescription())
                .withIcon("minecraft:ender_pearl")
                .withLocation(this.entryMap().get(location))
                .withCategoryToOpen(new ResourceLocation("modonomicon:hidden"));
    }

}
