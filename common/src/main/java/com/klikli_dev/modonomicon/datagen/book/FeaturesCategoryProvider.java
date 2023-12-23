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
                "___           _____5____________",
                "__(multiblock)______________d___",
                "___           _______r__a_______",
                "__c           __________________",
                "___           _______2___3___i__",
                "__s           _____e____________",
                "___           _______________g__",
                "___           _____f____________"
        };
    }

    @Override
    protected void generateEntries() {
        var multiblockEntry = this.add(this.makeMultiblockEntry("multiblock"));

        var conditionEntries = this.add(this.makeConditionEntries('r', 'a', '1', '2'));

        var recipeEntry = this.add(this.makeRecipeEntry('c'));

        var spotlightEntry = this.add(this.makeSpotlightEntry('s'));
        spotlightEntry.withParent(this.parent(recipeEntry));

        var emptyEntry = this.add(this.makeEmptyPageEntry('e'));
        emptyEntry.withParent(this.parent(spotlightEntry));

        var commandEntry = this.add(this.makeCommandEntry('f'));
        commandEntry.withParent(emptyEntry);
        commandEntry.withCommandToRunOnFirstRead(this.modLoc("test_command"));

        var entityEntry = this.add(this.makeEntityEntry('d'));

        var imageEntry = this.add(this.makeImageEntry('i'));
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

        this.context().page("demo_predicate");
        var demoPredicate = BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("demo_predicate"))
                .build();

        return this.entry(location)
                .withIcon(Items.FURNACE)
                .withLocation(this.entryMap().get(location))
                .withPages(multiBlockIntroPage, multiblockPreviewPage, multiblockPreviewPage2, demoPredicate);
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

        var advancementConditionEntry = this.entry(advancementLocation)
                .withIcon(Items.NETHER_STAR)
                .withEntryBackground(1, 0)
                .withCondition(advancementCondition)
                .withPages(advancementConditionPage);
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
                .withParent(this.parent(conditionRootEntry));
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

    private BookEntryModel makeImageEntry(char location) {
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

        return this.entry(location)
                .withIcon(Items.ITEM_FRAME)
                .withLocation(this.entryMap().get(location))
                .withPages(introPage, imagePage, testSpotlight, testImage);
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
