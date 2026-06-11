/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
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
    protected final Map<Identifier, BookModel> bookModels;
    protected final List<BookSubProvider> subProviders;
    protected final BookHierarchyResearchCompiler researchCompiler = new BookHierarchyResearchCompiler();
    private final LanguageProviderCache langCache;
    private final ResearchCache researchCache;


    public BookProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId,
                        List<BookSubProvider> subProviders) {
        this(packOutput, registries, modId, subProviders, null, null);
    }

    public BookProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId,
                        List<BookSubProvider> subProviders, LanguageProviderCache langCache, ResearchCache researchCache) {
        this.packOutput = packOutput;
        this.registries = registries;
        this.modId = modId;
        this.subProviders = subProviders;
        this.bookModels = new Object2ObjectOpenHashMap<>();
        this.langCache = langCache;
        this.researchCache = researchCache;
    }

    public String modId() {
        return this.modId;
    }

    protected Path getPath(Path dataFolder, BookModel bookModel) {
        Identifier id = bookModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(id.getPath() + "/book.json");
    }

    protected Path getThemePath(Path dataFolder, BookModel bookModel) {
        Identifier id = bookModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(id.getPath() + "/theme.json");
    }

    protected Path getPath(Path dataFolder, BookCategoryModel bookCategoryModel) {
        Identifier id = bookCategoryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCategoryModel.getBook().getId().getPath())
                .resolve("categories")
                .resolve(id.getPath() + ".json");
    }

    protected Path getPath(Path dataFolder, BookCommandModel bookCommandModel) {
        Identifier id = bookCommandModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookCommandModel.getBook().getId().getPath())
                .resolve("commands")
                .resolve(id.getPath() + ".json");
    }

    protected Path getPath(Path dataFolder, BookEntryModel bookEntryModel) {
        Identifier id = bookEntryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookEntryModel.getCategory().getBook().getId().getPath())
                .resolve("entries")
                .resolve(id.getPath() + ".json");
    }

    protected Path getPagePath(Path dataFolder, BookEntryModel bookEntryModel, String pageId) {
        Identifier id = bookEntryModel.getId();
        return dataFolder
                .resolve(id.getNamespace())
                .resolve(ModonomiconConstants.Data.MODONOMICON_DATA_PATH)
                .resolve(bookEntryModel.getCategory().getBook().getId().getPath())
                .resolve("entries")
                .resolve(id.getPath())
                .resolve("pages")
                .resolve(pageId + ".json");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.registries.thenCompose(registries -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            Path dataFolder = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

            if (this.langCache != null) {
                for (var subProvider : this.subProviders) {
                    if (subProvider instanceof ModonomiconProviderBase base) {
                        base.injectLang(this.langCache);
                    }
                }
            }

            this.subProviders.forEach(subProvider -> subProvider.generate(this.bookModels::put, registries));

            for (var bookModel : this.bookModels.values()) {
                var compiledResearch = this.researchCompiler.compile(bookModel);
                if (compiledResearch.isPresent()) {
                    if (this.researchCache != null) {
                        this.researchCache.accept(compiledResearch.get().bundleId(), compiledResearch.get().research());
                    } else {
                        futures.add(this.writeResearchBundle(cache, dataFolder, compiledResearch.get()));
                    }
                }

                Path bookPath = this.getPath(dataFolder, bookModel);

                if(!bookModel.dontGenerateJson()){ //a model from AddToBookSubProvider
                    futures.add(DataProvider.saveStable(cache, bookModel.toJson(registries), bookPath));
                }

                if (bookModel.getTheme() != null && bookModel.getTheme().shouldGenerateJson()) {
                    futures.add(DataProvider.saveStable(cache, bookModel.getTheme().toJson(), this.getThemePath(dataFolder, bookModel)));
                }


                for (var bookCategoryModel : bookModel.getCategories()) {
                    Path bookCategoryPath = this.getPath(dataFolder, bookCategoryModel);

                    if(!bookCategoryModel.dontGenerateJson()){ //a model from AddToCategorySubProvider
                        futures.add(DataProvider.saveStable(cache, bookCategoryModel.toJson(registries), bookCategoryPath));
                    }

                    for (var bookEntryModel : bookCategoryModel.getEntries()) {
                        Path bookEntryPath = this.getPath(dataFolder, bookEntryModel);
                        futures.add(DataProvider.saveStable(cache, bookEntryModel.toJson(registries), bookEntryPath));

                        if (bookEntryModel.generatePagesAsFiles()) {
                            for (var pageModel : bookEntryModel.getPages()) {
                                String pageId = pageModel.getId();
                                if (pageId == null || pageId.isEmpty()) {
                                    throw new IllegalStateException("Page in entry " + bookEntryModel.getId() + " has an empty ID, but generatePagesAsFiles is enabled.");
                                }
                                Path pagePath = this.getPagePath(dataFolder, bookEntryModel, pageId);
                                futures.add(DataProvider.saveStable(cache, pageModel.toJson(bookEntryModel.getId(), registries), pagePath));
                            }
                        }
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

    protected CompletableFuture<?> writeResearchBundle(CachedOutput cache, Path dataFolder, BookHierarchyResearchCompiler.CompiledBookResearch compiled) {
        var base = dataFolder.resolve(compiled.bundleId().getNamespace()).resolve(ModonomiconConstants.Data.RESEARCH_DATA_PATH).resolve(compiled.bundleId().getPath());
        var data = compiled.research();
        return CompletableFuture.allOf(
                this.save(cache, ResearchFactDefinition.CODEC, data.factDefinitions(), base.resolve("facts.json")),
                this.save(cache, ResearchNodeDefinition.CODEC, data.nodeDefinitions(), base.resolve("nodes.json")),
                this.save(cache, ResearchHookDefinition.CODEC, data.hookDefinitions(), base.resolve("hooks.json"))
        );
    }

    protected <T> CompletableFuture<?> save(CachedOutput cache, Codec<T> codec, List<T> values, Path path) {
        return DataProvider.saveStable(cache, this.list(codec, values), path);
    }

    protected <T> JsonElement list(Codec<T> codec, List<T> values) {
        JsonArray array = new JsonArray();
        for (T value : values) {
            array.add(codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow());
        }
        return array;
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
