/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.datagen.book.*;
import com.klikli_dev.modonomicon.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.datagen.book.page.BookTextPageModel;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BookGenerator implements DataProvider {
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

    private BookModel makeDemoBook(){
        var helper = new BookLangHelper(this.modid);
        helper.book("demo");
        helper.category("features");
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
        var multiblockEntry = BookEntryModel.builder()
                .withId(this.modLoc("features/multiblock"))
                .withName(helper.entryName())
                .withDescription(helper.entryDescription())
                .withIcon("minecraft:furnace")
                .withX(0).withY(0)
                .withPages(multiBlockIntroPage, multiblockPreviewPage)
                .build();

        var featuresCategory = BookCategoryModel.builder()
                .withId(this.modLoc("features"))
                .withName(helper.categoryName())
                .withIcon("minecraft:nether_star")
                .withEntries(multiblockEntry)
                .build();

        var demoBook = BookModel.builder()
                .withId(this.modLoc("demo"))
                .withName(helper.bookName())
                .withCategories(featuresCategory)
                .build();
        return demoBook;
    }

    private BookCategoryModel makeTestCat() {
        var page1 = BookTextPageModel.builder()
                .withText("modonomicon.test_cat.page1.text")
                .withTitle("modonomicon.test_cat.page1.title")
                .build();
        var testEntry = BookEntryModel.builder()
                .withId(this.modLoc("test_cat/test_entry"))
                .withName("modonomicon.test_cat.name")
                .withDescription("modonomicon.test_cat.description")
                .withIcon("minecraft:apple")
                .withPages(page1)
                .build();
        var testEntry2 = BookEntryModel.builder()
                .withId(this.modLoc("test_entry2"))
                .withName("modonomicon.test_cat2.name")
                .withDescription("modonomicon.test_cat2.description")
                .withIcon("minecraft:stick")
                .build();

        //link entries with lines
        testEntry.addParent(
                BookEntryParentModel.builder()
                        .withEntryId(testEntry2.getId())
                        .build()
        );

        var testCat = BookCategoryModel.builder()
                .withId(this.modLoc("test_cat"))
                .withName("modonomicon.test_cat.name")
                .withIcon("minecraft:apple")
                .withEntries(testEntry, testEntry2)
                .build();
        return testCat;
    }

    private BookModel add(BookModel bookModel) {
        if (this.bookModels.containsKey(bookModel.getId()))
            throw new IllegalStateException("Duplicate book " + bookModel.getId());
        this.bookModels.put(bookModel.getId(), bookModel);
        return bookModel;
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        Path folder = this.generator.getOutputFolder();
        this.start();

        for (var bookModel : this.bookModels.values()) {
            Path bookPath = getPath(folder, bookModel);
            try {
                DataProvider.saveStable(cache, bookModel.toJson(), bookPath);
            } catch (IOException exception) {
                Modonomicon.LOGGER.error("Couldn't save book {}", bookPath, exception);
            }

            for (var bookCategoryModel : bookModel.getCategories()) {
                Path bookCategoryPath = getPath(folder, bookCategoryModel);
                try {
                    DataProvider.saveStable(cache, bookCategoryModel.toJson(), bookCategoryPath);
                } catch (IOException exception) {
                    Modonomicon.LOGGER.error("Couldn't save book category {}", bookCategoryPath, exception);
                }

                for (var bookEntryModel : bookCategoryModel.getEntries()) {
                    Path bookEntryPath = getPath(folder, bookEntryModel);
                    try {
                        DataProvider.saveStable(cache, bookEntryModel.toJson(), bookEntryPath);
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
