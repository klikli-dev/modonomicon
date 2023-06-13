/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BookProvider implements DataProvider {

    protected static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final LanguageProvider lang;
    protected final Map<ResourceLocation, BookModel> bookModels;
    protected final String modid;

    /**
     * @param lang The LanguageProvider to fill with this book provider. IMPORTANT: the Languag Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public BookProvider(DataGenerator generator, String modid, LanguageProvider lang) {
        this.modid = modid;
        this.generator = generator;
        this.lang = lang;
        this.bookModels = new HashMap<>();
    }

    protected abstract void generate();

    protected ResourceLocation modLoc(String name) {
        return new ResourceLocation(this.modid, name);
    }

    protected BookModel add(BookModel bookModel) {
        if (this.bookModels.containsKey(bookModel.getId()))
            throw new IllegalStateException("Duplicate book " + bookModel.getId());
        this.bookModels.put(bookModel.getId(), bookModel);
        return bookModel;
    }

    private Path getPath(Path dataFolder, BookModel bookModel) {
        ResourceLocation id = bookModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(id.getPath() + "/book.json");
    }

    private Path getPath(Path dataFolder, BookCategoryModel bookCategoryModel) {
        ResourceLocation id = bookCategoryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCategoryModel.getBook().getId().getPath())
                .resolve("categories")
                .resolve(id.getPath() + ".json");
    }

    private Path getPath(Path dataFolder, BookCommandModel bookCommandModel) {
        ResourceLocation id = bookCommandModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCommandModel.getBook().getId().getPath())
                .resolve("commands")
                .resolve(id.getPath() + ".json");
    }

    private Path getPath(Path dataFolder, BookEntryModel bookEntryModel) {
        ResourceLocation id = bookEntryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookEntryModel.getCategory().getBook().getId().getPath())
                .resolve("entries")
                .resolve(id.getPath() + ".json");
    }

    @Override
    public void run(HashCache cache) throws IOException {
        Path dataFolder = this.generator.getOutputFolder().resolve("data");
        this.generate();

        for (var bookModel : this.bookModels.values()) {
            Path bookPath = this.getPath(dataFolder, bookModel);
            try {
                DataProvider.save(GSON, cache, bookModel.toJson(), bookPath);
            } catch (IOException exception) {
                LOGGER.error("Couldn't save book {}", bookPath, exception);
            }

            for (var bookCategoryModel : bookModel.getCategories()) {
                Path bookCategoryPath = this.getPath(dataFolder, bookCategoryModel);
                try {
                    DataProvider.save(GSON, cache, bookCategoryModel.toJson(), bookCategoryPath);
                } catch (IOException exception) {
                    LOGGER.error("Couldn't save book category {}", bookCategoryPath, exception);
                }

                for (var bookEntryModel : bookCategoryModel.getEntries()) {
                    Path bookEntryPath = this.getPath(dataFolder, bookEntryModel);
                    try {
                        DataProvider.save(GSON, cache, bookEntryModel.toJson(), bookEntryPath);
                    } catch (IOException exception) {
                        LOGGER.error("Couldn't save book entry {}", bookEntryPath, exception);
                    }
                }
            }

            for (var bookCommandModel : bookModel.getCommands()) {
                Path bookCommandPath = this.getPath(dataFolder, bookCommandModel);
                futures.add(DataProvider.saveStable(cache, bookCommandModel.toJson(), bookCommandPath));
            }
        }
    }

    @Override
    public String getName() {
        return "Books: " + this.modid;
    }

}
