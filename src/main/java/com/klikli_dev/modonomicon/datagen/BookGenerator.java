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

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(this.modid, name);
    }

    private void start() {
        var testBook = BookModel.builder()
                .withId(this.modLoc("test_book"))
                .withName("modonomicon.test_book.name")
                .build();
        this.add(testBook);
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
            Path path = getPath(folder, bookModel);
            try {
                DataProvider.save(GSON, cache, bookModel.toJson(), path);
            } catch (IOException exception) {
                Modonomicon.LOGGER.error("Couldn't save book {}", path, exception);
            }
        }
    }

    @Override
    public String getName() {
        return "Books: " + Modonomicon.MODID;
    }
}
