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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

public abstract class BookProvider implements DataProvider {

    protected static final Logger LOGGER = LogUtils.getLogger();
    
    public static final Collector<ModonomiconLanguageProvider, ?, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>> mapMaker
            = Collector.<ModonomiconLanguageProvider, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>, Object2ObjectOpenHashMap<String, ModonomiconLanguageProvider>>of(
                    Object2ObjectOpenHashMap::new,
                    (map, l) -> map.put(l.locale(), l),
                    (m1, m2) -> { m1.putAll(m2); return m1; },
                    (map) -> { map.trim(); return map; },
                    Characteristics.UNORDERED);

    protected final PackOutput packOutput;
    protected final ModonomiconLanguageProvider lang;
    protected final Map<String, ModonomiconLanguageProvider> translations;
    protected final Map<ResourceLocation, BookModel> bookModels;
    protected final String modid;
    protected String bookId;
    protected BookContextHelper context;

    protected Map<String, String> defaultMacros;

    protected ConditionHelper conditionHelper;

    /**
     * @param defaultLang The LanguageProvider to fill with this book provider. IMPORTANT: the Languag Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public BookProvider(String bookId, PackOutput packOutput, String modid, ModonomiconLanguageProvider defaultLang, ModonomiconLanguageProvider... translations) {
        this.modid = modid;
        this.packOutput = packOutput;
        this.lang = defaultLang;
        this.bookModels = new Object2ObjectOpenHashMap<>();
        this.translations = Stream.concat(Arrays.stream(translations), Stream.of(defaultLang))
                                  .collect(mapMaker);

        this.bookId = bookId;
        this.context = new BookContextHelper(this.modid);
        this.defaultMacros = new Object2ObjectOpenHashMap<>();
        this.conditionHelper = new ConditionHelper();
    }

    protected ModonomiconLanguageProvider lang() {
        return this.lang;
    }

    protected ModonomiconLanguageProvider lang(String locale) {
        return this.translations.get(locale);
    }

    public String bookId() {
        return this.bookId;
    }

    protected BookContextHelper context() {
        return this.context;
    }

    protected ConditionHelper condition() {
        return this.conditionHelper;
    }

    /**
     * Call registerMacro() here to make macros (= simple string.replace() of macro -> value) available to all category providers of this book.
     */
    protected abstract void registerDefaultMacros();

    /**
     * Override this to generate your book.
     * Each BookProvider should generate only one book.
     * Context already is set to the book id provided in the constructor.
     */
    protected abstract BookModel generateBook();

    /**
     * Register a macro (= simple string.replace() of macro -> value) to be used in all category providers of this book.
     */
    protected void registerDefaultMacro(String macro, String value) {
        this.defaultMacros.put(macro, value);
    }

    /**
     * Get the default macros (= simple string.replace() of macro -> value) to be used in all category providers of this book.
     */
    protected Map<String, String> defaultMacros() {
        return this.defaultMacros;
    }

    /**
     * Only override if you know what you are doing.
     * Generally you should not.
     */
    protected void generate() {
        this.context.book(this.bookId);
        this.add(this.generateBook());
    }

    protected ResourceLocation modLoc(String name) {
        return new ResourceLocation(this.modid, name);
    }

    protected BookModel add(BookModel bookModel) {
        if (this.bookModels.containsKey(bookModel.getId()))
            throw new IllegalStateException("Duplicate book " + bookModel.getId());
        this.bookModels.put(bookModel.getId(), bookModel);
        return bookModel;
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
    public CompletableFuture<?> run(CachedOutput cache) {

        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path dataFolder = this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);

        this.registerDefaultMacros();
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
