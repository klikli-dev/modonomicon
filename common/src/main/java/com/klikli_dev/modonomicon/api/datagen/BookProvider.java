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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The main provider class for book datagen. Implement a subprovider and hand it over to this provider to generate the files!
 */
public class BookProvider implements DataProvider {

    protected final String modId;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    protected final PackOutput packOutput;
    //This is a bit of a relic, one provider is only supposed to generate one book.
    protected final Map<ResourceLocation, BookModel> bookModels;
    protected final List<BookSubProvider> subProviders;


    public BookProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId,
                        List<BookSubProvider> subProviders) {
        this.packOutput = packOutput;
        this.registries = registries;
        this.modId = modId;
        this.subProviders = subProviders;
        this.bookModels = new Object2ObjectOpenHashMap<>();
    }

    public String modId() {
        return this.modId;
    }

    protected Path getPath(Path dataFolder, BookModel bookModel) {
        ResourceLocation id = bookModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(id.getPath() + "/book.json");
    }

    protected Path getPath(Path dataFolder, BookCategoryModel bookCategoryModel) {
        ResourceLocation id = bookCategoryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCategoryModel.getBook().getId().getPath())
                .resolve("categories")
                .resolve(id.getPath() + ".json");
    }

    protected Path getPath(Path dataFolder, BookCommandModel bookCommandModel) {
        ResourceLocation id = bookCommandModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCommandModel.getBook().getId().getPath())
                .resolve("commands")
                .resolve(id.getPath() + ".json");
    }

    protected Path getPath(Path dataFolder, BookEntryModel bookEntryModel) {
        ResourceLocation id = bookEntryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookEntryModel.getCategory().getBook().getId().getPath())
                .resolve("entries")
                .resolve(id.getPath() + ".json");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.registries.thenCompose(registries -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            Path dataFolder = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

            this.subProviders.forEach(subProvider -> subProvider.generate(this.bookModels::put, registries));

            for (var bookModel : this.bookModels.values()) {
                Path bookPath = this.getPath(dataFolder, bookModel);

                if(!bookModel.dontGenerateJson()){ //a model from AddToBookSubProvider
                    futures.add(DataProvider.saveStable(cache, bookModel.toJson(registries), bookPath));
                }


                for (var bookCategoryModel : bookModel.getCategories()) {
                    Path bookCategoryPath = this.getPath(dataFolder, bookCategoryModel);

                    if(!bookCategoryModel.dontGenerateJson()){ //a model from AddToCategorySubProvider
                        futures.add(DataProvider.saveStable(cache, bookCategoryModel.toJson(registries), bookCategoryPath));
                    }

                    for (var bookEntryModel : bookCategoryModel.getEntries()) {
                        Path bookEntryPath = this.getPath(dataFolder, bookEntryModel);
                        futures.add(DataProvider.saveStable(cache, bookEntryModel.toJson(registries), bookEntryPath));
                    }
                }

                for (var bookCommandModel : bookModel.getCommands()) {
                    Path bookCommandPath = this.getPath(dataFolder, bookCommandModel);
                    futures.add(DataProvider.saveStable(cache, bookCommandModel.toJson(registries), bookCommandPath));
                }
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "Books: " + this.modId();
    }

    protected BookModel add(BookModel bookModel) {
        if (this.bookModels.containsKey(bookModel.getId()))
            throw new IllegalStateException("Duplicate book " + bookModel.getId());
        this.bookModels.put(bookModel.getId(), bookModel);
        return bookModel;
    }
}
