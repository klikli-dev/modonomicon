/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
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

    private Path getPath(Path path, BookModel bookModel) {
        ResourceLocation id = bookModel.getId();
        return path.resolve("data/" + id.getNamespace() + "/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + id.getPath() + "/book.json");
    }

    private Path getPath(Path path, BookCategoryModel bookCategoryModel) {
        ResourceLocation id = bookCategoryModel.getId();
        return path.resolve("data/" + id.getNamespace() +
                        "/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + bookCategoryModel.getBook().getId().getPath() +
                "/categories/" + id.getPath() + ".json");
    }

    private Path getPath(Path path, BookEntryModel bookEntryModel) {
        ResourceLocation id = bookEntryModel.getId();
        return path.resolve("data/" + id.getNamespace() +
                "/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + bookEntryModel.getCategory().getBook().getId().getPath() +
                "/entries/" + id.getPath() + ".json");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {

        List<CompletableFuture<?>> futures = new ArrayList<>();


        Path folder = this.packOutput.getOutputFolder();

        this.generate();

        for (var bookModel : this.bookModels.values()) {
            Path bookPath = this.getPath(folder, bookModel);
            futures.add(DataProvider.saveStable(cache, bookModel.toJson(), bookPath));

            for (var bookCategoryModel : bookModel.getCategories()) {
                Path bookCategoryPath = this.getPath(folder, bookCategoryModel);
                futures.add(DataProvider.saveStable(cache, bookCategoryModel.toJson(), bookCategoryPath));

                for (var bookEntryModel : bookCategoryModel.getEntries()) {
                    Path bookEntryPath = this.getPath(folder, bookEntryModel);
                    futures.add(DataProvider.saveStable(cache, bookEntryModel.toJson(), bookEntryPath));
                }
            }
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Books: " + this.modid;
    }

}
