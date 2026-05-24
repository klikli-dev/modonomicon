/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.runtime;

import com.klikli_dev.modonomicon.api.book.runtime.RuntimeBookContentBatch;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeBookSelection;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeCategorySelection;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeEntrySelection;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RuntimeBookContentBatchImpl implements RuntimeBookContentBatch {

    private final List<RuntimeBookContentManager.Operation> operations = new ArrayList<>();
    private boolean finished;

    @Override
    public RuntimeBookSelection book(Identifier bookId) {
        this.ensureOpen();
        return new BookSelection(bookId);
    }

    @Override
    public void finish() {
        if (this.finished) {
            return;
        }

        this.finished = true;
        RuntimeBookContentManager.get().submitBatch(List.copyOf(this.operations));
    }

    @Override
    public void close() {
        this.finish();
    }

    private void ensureOpen() {
        if (this.finished) {
            throw new IllegalStateException("Runtime book content batch has already been finished");
        }
    }

    private final class BookSelection implements RuntimeBookSelection {
        private final Identifier bookId;

        private BookSelection(Identifier bookId) {
            this.bookId = bookId;
        }

        @Override
        public RuntimeBookSelection addCategory(BookCategory category) {
            RuntimeBookContentBatchImpl.this.ensureOpen();
            RuntimeBookContentBatchImpl.this.operations.add(new RuntimeBookContentManager.AddCategoryOperation(this.bookId, category));
            return this;
        }

        @Override
        public RuntimeCategorySelection category(Identifier categoryId) {
            RuntimeBookContentBatchImpl.this.ensureOpen();
            return new CategorySelection(this.bookId, categoryId);
        }
    }

    private final class CategorySelection implements RuntimeCategorySelection {
        private final Identifier bookId;
        private final Identifier categoryId;

        private CategorySelection(Identifier bookId, Identifier categoryId) {
            this.bookId = bookId;
            this.categoryId = categoryId;
        }

        @Override
        public RuntimeCategorySelection addEntry(BookEntry entry) {
            RuntimeBookContentBatchImpl.this.ensureOpen();
            RuntimeBookContentBatchImpl.this.operations.add(new RuntimeBookContentManager.AddEntryOperation(this.bookId, this.categoryId, entry));
            return this;
        }

        @Override
        public RuntimeEntrySelection entry(Identifier entryId) {
            RuntimeBookContentBatchImpl.this.ensureOpen();
            return new EntrySelection(this.bookId, this.categoryId, entryId);
        }
    }

    private final class EntrySelection implements RuntimeEntrySelection {
        private final Identifier bookId;
        private final Identifier categoryId;
        private final Identifier entryId;

        private EntrySelection(Identifier bookId, Identifier categoryId, Identifier entryId) {
            this.bookId = bookId;
            this.categoryId = categoryId;
            this.entryId = entryId;
        }

        @Override
        public RuntimeEntrySelection addPage(BookPage page) {
            RuntimeBookContentBatchImpl.this.ensureOpen();
            RuntimeBookContentBatchImpl.this.operations.add(new RuntimeBookContentManager.AddPageOperation(this.bookId, this.categoryId, this.entryId, page));
            return this;
        }
    }
}
