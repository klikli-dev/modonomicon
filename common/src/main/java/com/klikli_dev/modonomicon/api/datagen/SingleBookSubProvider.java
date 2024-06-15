/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An opinionated book sub provider with helper methods to generate a single book more easily.
 */
public abstract class SingleBookSubProvider extends ModonomiconProviderBase implements BookSubProvider {
    protected BookModel book;
    protected String bookId;
    protected int currentSortIndex;

    /**
     * @param defaultLang The LanguageProvider to fill with this book provider. IMPORTANT: the Language Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public SingleBookSubProvider(String bookId, String modId, ModonomiconLanguageProvider defaultLang, ModonomiconLanguageProvider... translations) {
        this(bookId, modId, defaultLang, makeLangMap(defaultLang, translations));
    }

    /**
     * @param defaultLang The LanguageProvider to fill with this book provider. IMPORTANT: the Language Provider needs to be added to the DataGenerator AFTER the BookProvider.
     */
    public SingleBookSubProvider(String bookId, String modId, ModonomiconLanguageProvider defaultLang, Map<String, ModonomiconLanguageProvider> translations) {
        super(modId, defaultLang, translations, new BookContextHelper(modId), new ConditionHelper());
        this.book = null;

        this.bookId = bookId;
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

    protected BookCategoryModel add(BookCategoryModel category) {
        if (category.getSortNumber() == -1) {
            category.withSortNumber(this.currentSortIndex++);
        }
        this.book.withCategory(category);
        return category;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, BookModel> consumer) {
        this.registerDefaultMacros();

        this.context().book(this.bookId());
        var book = BookModel.create(this.modLoc(this.bookId), this.context().bookName());

        this.add(this.context().bookName(), this.bookName());
        this.add(this.context().bookTooltip(), this.bookTooltip());
        var bookDescription = this.bookDescription();
        if (!StringUtil.isNullOrEmpty(bookDescription)) {
            this.add(this.context().bookDescription(), bookDescription);
            book.withDescription(this.context().bookDescription());
        }

        this.book = this.additionalSetup(book);

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

    /**
     * Implement this and modify the book as needed for additional config.
     * Categories should not be added here, instead call .add() in generateCategories().
     * Context already is set to this book.
     */
    protected BookModel additionalSetup(BookModel book) {
        return book;
    }

    /**
     * Implement this and return the book name in the main language.
     */
    protected abstract String bookName();

    /**
     * Implement this and return the book tooltip in the main language.
     */
    protected abstract String bookTooltip();

    /**
     * Implement this and return the book description in the main language.
     */
    protected String bookDescription() {
        return "";
    }
}
