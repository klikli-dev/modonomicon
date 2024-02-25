// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import com.klikli_dev.modonomicon.book.BookCategoryBackgroundParallaxLayer;
import com.klikli_dev.modonomicon.datagen.book.features.ImageEntryProvider;
import net.minecraft.Util;
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
                "___           ____5_____a_______",
                "__(multiblock)______t_______d___",
                "___           _______r__________",
                "__c           __________________",
                "___           _______2____1__i__",
                "__s           _____e____________",
                "___           _______________g__",
                "___           _____f____________"
        };
    }

    @Override
    protected void generateEntries() {
        var multiblockEntry = this.add(this.makeMultiblockEntry("multiblock"));

        var conditionEntries = this.add(this.makeConditionEntries('r', 'a', '1', '2'));

        var twoParentsEntry = this.add(this.makeEntryWithTwoParents('t'));
        twoParentsEntry.showWhenAnyParentUnlocked(true);
        var parent1 = conditionEntries.stream().filter(e -> e.getId().getPath().contains("condition_root")).findFirst().get();
        var parent2 = conditionEntries.stream().filter(e -> e.getId().getPath().contains("condition_level_2")).findFirst().get();
        twoParentsEntry.withParents(this.parent(parent1).withLineReversed(true), this.parent(parent2));
        twoParentsEntry.withCondition(this.condition().and(
                this.condition().entryRead(parent1),
                this.condition().entryRead(parent2)
        ));

        var recipeEntry = this.add(this.makeRecipeEntry('c'));

        var spotlightEntry = this.add(this.makeSpotlightEntry('s'));
        spotlightEntry.withParent(this.parent(recipeEntry).withLineReversed(true));

        var emptyEntry = this.add(this.makeEmptyPageEntry('e'));
        emptyEntry.withParent(this.parent(spotlightEntry));

        var commandEntry = this.add(this.makeCommandEntry('f'));
        commandEntry.withParent(emptyEntry);
        commandEntry.withCommandToRunOnFirstRead(this.modLoc("test_command"));

        var entityEntry = this.add(this.makeEntityEntry('d'));

        var imageEntry = new ImageEntryProvider(this).generate('i');
        imageEntry.withParent(this.parent(emptyEntry));

        var redirectEntry = this.add(this.makeRedirectEntry('5'));

        var customIconEntry = this.add(this.makeCustomIconEntry('g'));
        customIconEntry.withParent(this.parent(imageEntry));
    }

    @Override
    protected BookCategoryModel generateCategory() {
        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon(Items.NETHER_STAR)
                .withBackgroundParallaxLayers(
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/base.png"), 0.7f, -1),
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/1.png"), 1f, -1),
                        new BookCategoryBackgroundParallaxLayer(this.modLoc("textures/gui/parallax/flow/2.png"), 1.4f, -1)
                );
    }

    private BookEntryModel makeMultiblockEntry(String location) {
        this.context().entry("multiblock");

        this.context().page("intro");
        this.page(BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build());

        this.context().page("preview");
        this.page(BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("blockentity"))
                .withMultiblockName("multiblocks.modonomicon.blockentity")
                .withText(this.context().pageText())
                .build());

        this.page("preview2", () -> BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("tag"))
                .withText(this.context().pageText())
                .build());

        this.lang().add(this.context().pageText(), "A multiblock with tag!");

        this.context().page("demo_predicate");
        this.page(BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("demo_predicate"))
                .build());

        this.context().page("demo_fluid");
        this.page(BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("demo_fluid"))
                .build());

//        this.context().page("crash_test");
//        this.page(BookMultiblockPageModel.builder()
//                .withMultiblockId(this.modLoc("crash_test"))
//                .build());

        return this.entry(location)
                .withIcon(Items.FURNACE)
                .withLocation(this.entryMap().get(location));
    }

    private BookEntryModel makeEntryWithTwoParents(char location) {
        this.context().entry("two_parents");
        this.add(this.context().entryName(), "Entry with two parents");
        this.add(this.context().entryDescription(), "Yup, two parents!");

        this.context().page("intro");
        this.page(BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build());

        this.add(this.context().pageTitle(), "Entry with two parents");
        this.add(this.context().pageText(), "It has two parents ..");

        return this.entry(location)
                .withIcon(Items.AMETHYST_CLUSTER)
                .withLocation(this.entryMap().get(location));
    }


    private List<BookEntryModel> makeConditionEntries(char rootLocation, char advancementLocation, char level1Location, char level2Location) {
        var result = new ArrayList<BookEntryModel>();

        this.context().entry("condition_root");
        this.context().page("info");
        var conditionRootEntryInfoPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        var conditionRootEntry = this.entry(rootLocation)
                .withIcon(Items.REDSTONE_TORCH)
                .withEntryBackground(1, 0)
                .withPages(conditionRootEntryInfoPage);
        result.add(conditionRootEntry);

        this.context().entry("condition_advancement");
        this.context().page("info");
        var advancementConditionPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        this.lang().add(this.context().pageTitle(), "Advancement Condition");
        this.lang().add(this.context().pageText(), "Advancement Conditions unlock, as the name implies, if a player has an advancement.");

        var advancementCondition = BookAdvancementConditionModel.builder()
                .withAdvancementId(new ResourceLocation("minecraft:husbandry/ride_a_boat_with_a_goat"))
                .build();

        this.lang().add(
                Util.makeDescriptionId("advancement", advancementCondition.getAdvancementId()) + ".title",
                "Ride a Boat with a Goat"
        );

        var pageCondition = BookAdvancementConditionModel.builder()
                .withAdvancementId(new ResourceLocation("minecraft:story/mine_stone"))
                .build();

        this.context().page("conditional_page");
        var actualConditionPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .withCondition(pageCondition)
                .build();
        this.lang().add(this.context().pageTitle(), "Conditional Pages");
        this.lang().add(this.context().pageText(), "Conditional pages unlock if a player has satisfied their condition.");

        var advancementConditionEntry = this.entry(advancementLocation)
                .withIcon(Items.NETHER_STAR)
                .withEntryBackground(1, 0)
                .withCondition(advancementCondition)
                .withPages(advancementConditionPage, actualConditionPage);
        result.add(advancementConditionEntry);

        this.context().entry("condition_level_1");
        this.context().page("info");
        var conditionLevel1EntryInfoPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();
        var conditionLevel1EntryCondition = BookEntryReadConditionModel.builder()
                .withEntry(conditionRootEntry.getId())
                .build();
        var conditionLevel1Entry = this.entry(level1Location)
                .withIcon(Items.LEVER)
                .withPages(conditionLevel1EntryInfoPage)
                .withCondition(conditionLevel1EntryCondition)
                .withParent(this.parent(conditionRootEntry).withLineReversed(true));
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
        var conditionLevel2Entry = this.entry(level2Location)
                .withIcon(Items.TORCH)
                .withPages(conditionLevel2EntryInfoPage)
                .withCondition(conditionLevel2EntryCondition)
                .withParent(this.parent(conditionLevel1Entry));
        result.add(conditionLevel2Entry);

        return result;
    }

    private BookEntryModel makeRecipeEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.CRAFTING_TABLE)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, crafting, smelting, smoking, blasting, campfireCooking, stonecutting, smithing, missing);
    }

    private BookEntryModel makeSpotlightEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.BEACON)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, spotlight1, spotlight2);
    }

    private BookEntryModel makeEmptyPageEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.OBSIDIAN)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, empty, empty2);
    }

    private BookEntryModel makeCommandEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.OAK_SIGN)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, commandLink);
    }


    private BookEntryModel makeEntityEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.SPIDER_EYE)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, entity1, entity2);
    }

    private BookEntryModel makeCustomIconEntry(char location) {
        this.context().entry("custom_icon");
        this.lang().add(this.context().entryName(), "Custom Icon");
        this.lang().add(this.context().entryDescription(), "This entry has a custom texture with size 32x32 as icon!");

        this.context().page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(this.context().pageText())
                .withTitle(this.context().pageTitle())
                .build();

        this.lang().add(this.context().pageTitle(), "Custom Icon");
        this.lang().add(this.context().pageText(), "This entry has a custom texture with size 32x32 as icon!");

        return this.entry(location)
                .withIcon(this.modLoc("textures/gui/big_test_icon.png"), 32, 32)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage);
    }

    private BookEntryModel makeRedirectEntry(char location) {
        this.context().entry("redirect");

        return this.entry(location)
                .withIcon(Items.ENDER_PEARL)
                .withLocation(this.entryMap().get(location))
                .withCategoryToOpen(new ResourceLocation("modonomicon:hidden"));
    }

}
