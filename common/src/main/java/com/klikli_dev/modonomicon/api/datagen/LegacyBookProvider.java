/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A book sub provider that is oriented on the legacy book provider API for easier migration.
 * It still needs to be handed over to a book provider!
 */
public abstract class LegacyBookProvider extends ModonomiconProviderBase implements BookSubProvider {
    protected BookModel book;
    protected String bookId;
    protected int currentSortIndex;

    /**
     * Creates a legacy book subprovider.
     * <p>
     * Language access is provided via setup injection at generate time.
     *
     * @param bookId the book id
     * @param modId  the mod id
     */
    public LegacyBookProvider(String bookId, String modId) {
        super(modId, null, Map.of(), new BookContextHelper(modId), new ConditionHelper());
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

    @Override
    public void generate(BiConsumer<Identifier, BookModel> consumer, HolderLookup.Provider registries) {
        this.registries(registries);
        this.registerDefaultMacros();

        this.context().book(this.bookId());
        this.book = this.generateBook();

        consumer.accept(this.book.getId(), this.book);
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
}
