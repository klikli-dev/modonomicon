/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.datagen.book.BookModel;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
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
        var testCat = makeTestCat();
        var testBook = BookModel.builder()
                .withId(this.modLoc("test_book"))
                .withName("modonomicon.test_book.name")
                .withCategories(testCat)
                .build();
        this.add(testBook);
    }

    private BookCategoryModel makeTestCat(){
        var testEntry = BookEntryModel.builder()
                .withId(this.modLoc("test_cat/test_entry"))
                .withName("modonomicon.test_cat.name")
                .withDescription("modonomicon.test_cat.description")
                .withIcon("minecraft:apple")
                .build();
        var testEntry2 = BookEntryModel.builder()
                .withId(this.modLoc("test_entry2"))
                .withName("modonomicon.test_cat2.name")
                .withDescription("modonomicon.test_cat2.description")
                .withIcon("minecraft:stick")
                .build();
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

            for(var bookCategoryModel : bookModel.getCategories()){
                Path bookCategoryPath = getPath(folder, bookCategoryModel);
                try {
                    DataProvider.save(GSON, cache, bookCategoryModel.toJson(), bookCategoryPath);
                } catch (IOException exception) {
                    Modonomicon.LOGGER.error("Couldn't save book category {}", bookCategoryPath, exception);
                }

                for(var bookEntryModel : bookCategoryModel.getEntries()){
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
