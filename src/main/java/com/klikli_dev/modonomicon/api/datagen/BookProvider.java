/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BookProvider implements DataProvider {

    protected static final Logger LOGGER = LogUtils.getLogger();

    protected final PackOutput packOutput;
    protected final LanguageProvider lang;
    protected final Map<ResourceLocation, BookModel> bookModels;
    protected final String modid;

    /**
     * @param lang The LanguageProvider to fill with this book provider. IMPORTANT: the Languag Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public BookProvider(PackOutput packOutput, String modid, LanguageProvider lang) {
        this.modid = modid;
        this.packOutput = packOutput;
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
    public CompletableFuture<?> run(CachedOutput cache) {

        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path dataFolder = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

        this.generate();

        for (var bookModel : this.bookModels.values()) {
            Path bookPath = this.getPath(dataFolder, bookModel);
            futures.add(DataProvider.saveStable(cache, bookModel.toJson(), bookPath));

            for (var bookCategoryModel : bookModel.getCategories()) {
                Path bookCategoryPath = this.getPath(dataFolder, bookCategoryModel);
                futures.add(DataProvider.saveStable(cache, bookCategoryModel.toJson(), bookCategoryPath));

                for (var bookEntryModel : bookCategoryModel.getEntries()) {
                    Path bookEntryPath = this.getPath(dataFolder, bookEntryModel);
                    futures.add(DataProvider.saveStable(cache, bookEntryModel.toJson(), bookEntryPath));
                }
            }

            for (var bookCommandModel : bookModel.getCommands()) {
                Path bookCommandPath = this.getPath(dataFolder, bookCommandModel);
                futures.add(DataProvider.saveStable(cache, bookCommandModel.toJson(), bookCommandPath));
            }
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Books: " + this.modid;
    }

}
