/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.datagen.BookLangHelper;
import com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadCondition;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryUnlockedCondition;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookFalseConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookGenerator implements DataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Map<ResourceLocation, BookModel> bookModels;
    protected String modid;

    public BookGenerator(DataGenerator generator, String modid) {
        this.modid = modid;
        this.generator = generator;
        this.bookModels = new HashMap<>();
    }

    private static Path getPath(Path path, BookModel bookModel) {
        ResourceLocation id = bookModel.getId();
        return path.resolve("data/" + id.getNamespace() + "/modonomicons/" + id.getPath() + "/book.json");
    }

    private static Path getPath(Path path, BookCategoryModel bookCategoryModel) {
        ResourceLocation id = bookCategoryModel.getId();
        return path.resolve("data/" + id.getNamespace() +
                "/modonomicons/" + bookCategoryModel.getBook().getId().getPath() +
                "/categories/" + id.getPath() + ".json");
    }

    private static Path getPath(Path path, BookEntryModel bookEntryModel) {
        ResourceLocation id = bookEntryModel.getId();
        return path.resolve("data/" + id.getNamespace() +
                "/modonomicons/" + bookEntryModel.getCategory().getBook().getId().getPath() +
                "/entries/" + id.getPath() + ".json");
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(this.modid, name);
    }

    private void start() {
        var demoBook = this.makeDemoBook();
        this.add(demoBook);
    }

    private BookModel makeDemoBook() {
        var helper = ModonomiconAPI.get().getLangHelper(this.modid);
        helper.book("demo");

        var featuresCategory = this.makeFeaturesCategory(helper);
        var formattingCategory = this.makeFormattingCategory(helper);

        var demoBook = BookModel.builder()
                .withId(this.modLoc("demo"))
                .withName(helper.bookName())
                .withTooltip(helper.bookTooltip())
                .withCategory(featuresCategory)
                .withCategory(formattingCategory)
                .build();
        return demoBook;
    }

    private BookCategoryModel makeFeaturesCategory(BookLangHelper helper) {
        helper.category("features");

        var entryHelper = ModonomiconAPI.get().getEntryLocationHelper();
        entryHelper.setMap(
                "_____________________",
                "__m______________d___",
                "__________r__________",
                "__c__________________",
                "__________2___3___i__",
                "__s_____e____________"
        );

        var multiblockEntry = this.makeMultiblockEntry(helper, entryHelper);

        var conditionEntries = this.makeConditionEntries(helper, entryHelper);

        var recipeEntry = this.makeRecipeEntry(helper, entryHelper).build();

        var spotlightEntry = this.makeSpotlightEntry(helper, entryHelper);
        spotlightEntry.withParent(BookEntryParentModel.builder().withEntryId(recipeEntry.getId()).build());

        var emptyEntry = this.makeEmptyPageEntry(helper, entryHelper);
        emptyEntry.withParent(BookEntryParentModel.builder().withEntryId(spotlightEntry.id).build());

        var entityEntry = this.makeEntityEntry(helper, entryHelper);

        var imageEntry = this.makeImageEntry(helper, entryHelper);
        imageEntry.withParent(BookEntryParentModel.builder().withEntryId(emptyEntry.id).build());

        return BookCategoryModel.builder()
                .withId(this.modLoc("features"))
                .withName(helper.categoryName())
                .withIcon("minecraft:nether_star")
                .withEntry(multiblockEntry)
                .withEntry(recipeEntry)
                .withEntries(conditionEntries)
                .withEntry(spotlightEntry.build())
                .withEntry(emptyEntry.build())
                .withEntry(entityEntry.build())
                .withEntry(imageEntry.build())
                .build();
    }

    private BookEntryModel makeMultiblockEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("multiblock");

        helper.page("intro");
        var multiBlockIntroPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("preview");
        var multiblockPreviewPage = BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("blockentity"))
                .withMultiblockName("multiblocks.modonomicon.blockentity")
                .withText(helper.pageText())
                .build();

        helper.page("preview2");
        var multiblockPreviewPage2 = BookMultiblockPageModel.builder()
                .withMultiblockId(this.modLoc("tag"))
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/multiblock"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:furnace")
                .withLocation(entryHelper.get('m'))
                .withPages(multiBlockIntroPage, multiblockPreviewPage, multiblockPreviewPage2)
                .build();
    }

    private List<BookEntryModel> makeConditionEntries(BookLangHelper helper, EntryLocationHelper entryHelper) {
        var result = new ArrayList<BookEntryModel>();

        helper.entry("condition_root");
        helper.page("info");
        var conditionRootEntryInfoPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();
        var conditionRootEntry = BookEntryModel.builder()
                .withId(this.modLoc("features/condition_root"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:redstone_torch")
                .withLocation(entryHelper.get('r'))
                .withEntryBackground(1, 0)
                .withPages(conditionRootEntryInfoPage)
                .build();
        result.add(conditionRootEntry);

        helper.entry("condition_level_1");
        helper.page("info");
        var conditionLevel1EntryInfoPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();
        var conditionLevel1EntryCondition = BookEntryReadCondition.builder()
                .withEntry(conditionRootEntry.getId())
                .build();
        var conditionLevel1Entry = BookEntryModel.builder()
                .withId(this.modLoc("features/condition_level_1"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:lever")
                .withLocation(entryHelper.get('2'))
                .withPages(conditionLevel1EntryInfoPage)
                .withCondition(conditionLevel1EntryCondition)
                .withParent(BookEntryParentModel.builder().withEntryId(conditionRootEntry.getId()).build())
                .build();
        result.add(conditionLevel1Entry);

        helper.entry("condition_level_2");
        helper.page("info");
        var conditionLevel2EntryInfoPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();
        var conditionLevel2EntryCondition = BookEntryUnlockedCondition.builder()
                .withEntry(conditionLevel1Entry.getId())
                .build();
        var conditionLevel2Entry = BookEntryModel.builder()
                .withId(this.modLoc("features/condition_level_2"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:torch")
                .withLocation(entryHelper.get('3'))
                .withPages(conditionLevel2EntryInfoPage)
                .withCondition(conditionLevel2EntryCondition)
                .withParent(BookEntryParentModel.builder().withEntryId(conditionLevel1Entry.getId()).build())
                .build();
        result.add(conditionLevel2Entry);

        return result;
    }

    private BookEntryModel.Builder makeRecipeEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("recipe");

        helper.page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("crafting");
        var crafting = BookCraftingRecipePageModel.builder()
                .withRecipeId1("minecraft:crafting_table")
                .withRecipeId2("minecraft:oak_planks")
                .withText(helper.pageText())
                .withTitle2("test.test.test")
                .build();

        helper.page("smelting");
        var smelting = BookSmeltingRecipePageModel.builder()
                .withRecipeId1("minecraft:charcoal")
                .withRecipeId2("minecraft:cooked_beef")
                .build();

        helper.page("smoking");
        var smoking = BookSmokingRecipePageModel.builder()
                .withRecipeId1("minecraft:cooked_beef_from_smoking")
                .withText(helper.pageText())
                .build();

        helper.page("blasting");
        var blasting = BookBlastingRecipePageModel.builder()
                .withRecipeId2("minecraft:iron_ingot_from_blasting_iron_ore")
                .build();

        helper.page("campfire_cooking");
        var campfireCooking = BookCampfireCookingRecipePageModel.builder()
                .withRecipeId1("minecraft:cooked_beef_from_campfire_cooking")
                .build();

        helper.page("stonecutting");
        var stonecutting = BookStonecuttingRecipePageModel.builder()
                .withRecipeId1("minecraft:andesite_slab_from_andesite_stonecutting")
                .build();

        helper.page("smithing");
        var smithing = BookSmithingRecipePageModel.builder()
                .withRecipeId1("minecraft:netherite_axe_smithing")
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/recipe"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:crafting_table")
                .withLocation(entryHelper.get('c'))
                .withPages(introPage, crafting, smelting, smoking, blasting, campfireCooking, stonecutting, smithing);
    }

    private BookEntryModel.Builder makeSpotlightEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("spotlight");

        helper.page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("spotlight1");
        var spotlight1 = BookSpotlightPageModel.builder()
                .withTitle(helper.pageTitle())
                .withText(helper.pageText())
                .withItem(Ingredient.of(Items.APPLE))
                .build();

        helper.page("spotlight2");
        var spotlight2 = BookSpotlightPageModel.builder()
                .withText(helper.pageText())
                .withItem(Ingredient.of(Items.DIAMOND))
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/spotlight"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:beacon")
                .withLocation(entryHelper.get('s'))
                .withPages(introPage, spotlight1, spotlight2);
    }

    private BookEntryModel.Builder makeEmptyPageEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("empty");

        helper.page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("empty");
        var empty = BookEmptyPageModel.builder()
                .build();

        helper.page("empty2");
        var empty2 = BookEmptyPageModel.builder()
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/empty"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:obsidian")
                .withLocation(entryHelper.get('e'))
                .withPages(introPage, empty, empty2);
    }

    private BookEntryModel.Builder makeEntityEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("entity");

        helper.page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("entity1");
        var entity1 = BookEntityPageModel.builder()
                .withEntityName(helper.pageTitle())
                .withEntityId("minecraft:ender_dragon")
                .withScale(0.5f)
                .build();

        helper.page("entity2");
        var entity2 = BookEntityPageModel.builder()
                .withText(helper.pageText())
                .withEntityId("minecraft:spider")
                .withScale(1f)
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/entity"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:spider_eye")
                .withLocation(entryHelper.get('d'))
                .withPages(introPage, entity1, entity2);
    }

    private BookEntryModel.Builder makeImageEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("image");

        helper.page("intro");
        var introPage = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build();

        helper.page("image");
        var imagePage = BookImagePageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .withImages(
                        new ResourceLocation("modonomicon:textures/gui/default_background.png"),
                        new ResourceLocation("modonomicon:textures/gui/dark_slate_seamless.png")
                )
                .build();

        return BookEntryModel.builder()
                .withId(this.modLoc("features/image"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:item_frame")
                .withLocation(entryHelper.get('i'))
                .withPages(introPage, imagePage);
    }

    private BookCategoryModel makeFormattingCategory(BookLangHelper helper) {
        helper.category("formatting");

        var entryHelper = new EntryLocationHelper();
        entryHelper.setMap(
                "_____________________",
                "__b___a______________",
                "__________l_____x____",
                "_____________________",
                "_____________________"
        );

        var basicFormattingEntry = this.makeBasicFormattingEntry(helper, entryHelper);
        var advancedFormattingEntry = this.makeAdvancedFormattingEntry(helper, entryHelper);
        var linkFormattingEntry = this.makeLinkFormattingEntry(helper, entryHelper);
        var alwaysLockedEntry = this.makeAlwaysLockedEntry(helper, entryHelper);
        alwaysLockedEntry.withCondition(BookFalseConditionModel.builder().build());

        linkFormattingEntry.withParent(BookEntryParentModel.builder().withEntryId(advancedFormattingEntry.id).build());
        advancedFormattingEntry.withParent(BookEntryParentModel.builder().withEntryId(basicFormattingEntry.id).build());

        return BookCategoryModel.builder()
                .withId(this.modLoc("formatting"))
                .withName(helper.categoryName())
                .withIcon("minecraft:book")
                .withEntry(basicFormattingEntry.build())
                .withEntry(advancedFormattingEntry.build())
                .withEntry(linkFormattingEntry.build())
                .withEntry(alwaysLockedEntry.build())
                .build();
    }

    private BookEntryModel.Builder makeBasicFormattingEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {

        helper.entry("basic");
        helper.page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build(); //bold, italics, underlines,

        helper.page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(helper.pageText())
                //.withTitle(helper.pageTitle())
                .build(); //strikethrough, color

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc("formatting/basic"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:paper")
                .withLocation(entryHelper.get('b'))
                .withPage(page1)
                .withPage(page2);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeAdvancedFormattingEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("advanced");
        helper.page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build(); //translatable texts, mixed formatting

        helper.page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(helper.pageText())
                //.withTitle(helper.pageTitle())
                .build(); //lists

        helper.page("page3");
        var page3 = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build(); //lists

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc("formatting/advanced"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:feather")
                .withLocation(entryHelper.get('a'))
                .withEntryBackground(0, 1)
                .withPage(page1)
                .withPage(page2)
                .withPage(page3);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeLinkFormattingEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("link");
        helper.page("page1");
        var page1 = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build(); //http links

        helper.page("page2");
        var page2 = BookTextPageModel.builder()
                .withText(helper.pageText())
                .withTitle(helper.pageTitle())
                .build(); //book entry links

        var formattingEntry = BookEntryModel.builder()
                .withId(this.modLoc("formatting/link"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:writable_book")
                .withLocation(entryHelper.get('l'))
                .withEntryBackground(0, 2)
                .withPage(page1)
                .withPage(page2);

        return formattingEntry;
    }

    private BookEntryModel.Builder makeAlwaysLockedEntry(BookLangHelper helper, EntryLocationHelper entryHelper) {
        helper.entry("always_locked");

        var entry = BookEntryModel.builder()
                .withId(this.modLoc(helper.category + "/" + helper.entry))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:nether_star")
                .withLocation(entryHelper.get('x'))
                .withEntryBackground(0, 1);

        return entry;
    }


    private BookModel add(BookModel bookModel) {
        if (this.bookModels.containsKey(bookModel.getId()))
            throw new IllegalStateException("Duplicate book " + bookModel.getId());
        this.bookModels.put(bookModel.getId(), bookModel);
        return bookModel;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        Path folder = this.generator.getOutputFolder();
        this.start();

        for (var bookModel : this.bookModels.values()) {
            Path bookPath = getPath(folder, bookModel);
            try {
                DataProvider.save(GSON, cache, bookModel.toJson(), bookPath);
            } catch (IOException exception) {
                Modonomicon.LOGGER.error("Couldn't save book {}", bookPath, exception);
            }

            for (var bookCategoryModel : bookModel.getCategories()) {
                Path bookCategoryPath = getPath(folder, bookCategoryModel);
                try {
                    DataProvider.save(GSON, cache, bookCategoryModel.toJson(), bookCategoryPath);
                } catch (IOException exception) {
                    Modonomicon.LOGGER.error("Couldn't save book category {}", bookCategoryPath, exception);
                }

                for (var bookEntryModel : bookCategoryModel.getEntries()) {
                    Path bookEntryPath = getPath(folder, bookEntryModel);
                    try {
                        DataProvider.save(GSON, cache, bookEntryModel.toJson(), bookEntryPath);
                    } catch (IOException exception) {
                        Modonomicon.LOGGER.error("Couldn't save book entry {}", bookEntryPath, exception);
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Books: " + Modonomicon.MODID;
    }
}
