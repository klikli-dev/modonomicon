/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An opinionated book sub provider with helper methods to generate contents for an existing book more easily. E.g. for use in a mod that adds to a book from another mod, or modpacks that use datapacks to add to a book.
 *
 * Generates book categories, but no book.json
 */
public abstract class AddToBookSubProvider extends ModonomiconProviderBase implements BookSubProvider {
    protected BookModel book;
    protected String bookId;
    protected int currentSortIndex;

    /**
     * @param targetBook  The ResourceLocation (modid and book name) of the book to add to.
     * @param defaultLang The LanguageProvider to fill with this book provider. IMPORTANT: the Language Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public AddToBookSubProvider(ResourceLocation targetBook, BiConsumer<String, String> defaultLang) {
        this(targetBook, defaultLang, Map.of());
    }

    /**
     * @param targetBook  The ResourceLocation (modid and book name) of the book to add to.
     * @param defaultLang The LanguageProvider to fill with this book provider. IMPORTANT: the Language Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public AddToBookSubProvider(ResourceLocation targetBook, BiConsumer<String, String> defaultLang, Map<String, BiConsumer<String, String>> translations) {
        super(targetBook.getNamespace(), defaultLang, translations, new BookContextHelper(targetBook.getNamespace()), new ConditionHelper());
        this.book = null;

        this.bookId = targetBook.getPath();
        this.currentSortIndex = 0;
    }

    public String bookId() {
        return this.bookId;
    }

    /**
     * Register a macro (= simple string.replace() of macro -> value) to be used in all category providers of this book.
     */
    protected void registerDefaultMacro(String macro, String value) {
        this.registerMacro(macro, value);
    }

    /**
     * Allows to set the current sort index so categories added to the target book are sorted correctly.
     */
    protected void currentSortIndex(int index) {
        this.currentSortIndex = index;
    }

    protected BookCategoryModel add(BookCategoryModel category) {
        if (category.getSortNumber() == -1) {
            category.withSortNumber(this.currentSortIndex++);
        }
        this.book.withCategory(category);
        return category;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, BookModel> consumer, HolderLookup.Provider registries) {
        this.registries(registries);

        this.registerDefaultMacros();

        this.context().book(this.bookId());
        this.book = BookModel.create(this.modLoc(this.bookId), this.context().bookName());
        this.book.withDontGenerateJson(true);

        this.generateCategories();

        consumer.accept(this.book.getId(), this.book);
    }

    /**
     * Call registerMacro() here to make macros (= simple string.replace() of macro -> value) available to all category providers of this book.
     */
    protected abstract void registerDefaultMacros();

    /**
     * Implement this and in it generate and .add() your categories.
     * Context already is set to this book.
     */
    protected abstract void generateCategories();
}
