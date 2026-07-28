/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.stub;


import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeBookContentBatch;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeBookSelection;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeCategorySelection;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeEntrySelection;
import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModonomiconAPIStub implements ModonomiconAPI {
    private static final ModonomiconAPIStub instance = new ModonomiconAPIStub();
    private static final NoOpRuntimeBatch NO_OP_RUNTIME_BATCH = new NoOpRuntimeBatch();

    private ModonomiconAPIStub() {
    }

    public static ModonomiconAPIStub get() {
        return instance;
    }

    @Override
    public boolean isStub() {
        return true;
    }

    @Override
    public BookContextHelper getContextHelper(String modid) {
        return null;
    }

    @Override
    public CategoryEntryMap getEntryMap() {
        return null;
    }

    @Override
    public Multiblock getMultiblock(Identifier id) {
        return null;
    }

    @Override
    public RuntimeBookContentBatch openRuntimeContentBatch() {
        return NO_OP_RUNTIME_BATCH;
    }

    @Override
    public @Nullable MultiblockPreviewData getCurrentPreviewMultiblock() {
        return null;
    }

    private static final class NoOpRuntimeBatch implements RuntimeBookContentBatch, RuntimeBookSelection, RuntimeCategorySelection, RuntimeEntrySelection {

        @Override
        public RuntimeBookSelection book(Identifier bookId) {
            return this;
        }

        @Override
        public RuntimeBookSelection addCategory(BookCategory category) {
            return this;
        }

        @Override
        public RuntimeCategorySelection category(Identifier categoryId) {
            return this;
        }

        @Override
        public RuntimeCategorySelection addEntry(BookEntry entry) {
            return this;
        }

        @Override
        public RuntimeEntrySelection entry(Identifier entryId) {
            return this;
        }

        @Override
        public RuntimeEntrySelection addPage(BookPage page) {
            return this;
        }

        @Override
        public void finish() {
        }

        @Override
        public void close() {
        }
    }
}
