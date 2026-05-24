/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.runtime;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.data.BookDataManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RuntimeBookContentManager {

    private static final RuntimeBookContentManager instance = new RuntimeBookContentManager();

    private final List<StoredOperation> operations = new ArrayList<>();
    private int generation;

    private RuntimeBookContentManager() {
    }

    public static RuntimeBookContentManager get() {
        return instance;
    }

    public synchronized void submitBatch(List<Operation> batchOperations) {
        if (batchOperations.isEmpty()) {
            return;
        }

        for (var operation : batchOperations) {
            this.operations.add(new StoredOperation(this.cloneOperation(operation)));
        }

        this.tryApplyPendingOperations();
    }

    public synchronized void onBooksPreLoad() {
        this.generation++;
    }

    public synchronized void onBooksLoaded() {
        this.tryApplyPendingOperations();
    }

    private void tryApplyPendingOperations() {
        var bookDataManager = BookDataManager.get();
        if (!bookDataManager.isLoaded()) {
            return;
        }

        var changedBooks = new LinkedHashSet<Identifier>();
        for (var storedOperation : this.operations) {
            if (storedOperation.discarded || storedOperation.appliedGeneration == this.generation) {
                continue;
            }

            var result = this.applyOperation(storedOperation);
            if (result == ApplyResult.APPLIED) {
                changedBooks.add(storedOperation.operation.bookId());
            }
        }

        if (!changedBooks.isEmpty() && bookDataManager.areBooksBuilt()) {
            bookDataManager.rebuildBooks(changedBooks);
            bookDataManager.syncBooks(changedBooks);
        }
    }

    private ApplyResult applyOperation(StoredOperation storedOperation) {
        var bookDataManager = BookDataManager.get();
        var bookId = storedOperation.operation.bookId();
        var book = bookDataManager.getBook(bookId);
        if (book == null) {
            return ApplyResult.PENDING;
        }

        var validationError = this.validateOperation(book, storedOperation.operation);
        if (validationError.isSuccess()) {
            this.applyResolvedOperation(book, storedOperation.operation);
            storedOperation.appliedGeneration = this.generation;
            return ApplyResult.APPLIED;
        }

        if (validationError.isPending()) {
            return ApplyResult.PENDING;
        }

        this.reportError(bookId, validationError.message());
        storedOperation.discarded = true;
        return ApplyResult.FAILED;
    }

    private ValidationResult validateOperation(Book book, Operation operation) {
        if (operation instanceof AddCategoryOperation addCategory) {
            if (book.getCategory(addCategory.category().getId()) != null) {
                return ValidationResult.failure("Cannot add runtime category '" + addCategory.category().getId() + "' because it already exists in book '" + book.getId() + "'");
            }

            for (var entry : addCategory.entries()) {
                if (!addCategory.category().getId().equals(entry.getCategoryId())) {
                    return ValidationResult.failure("Cannot add runtime category '" + addCategory.category().getId() + "' because contained entry '" + entry.getId() + "' targets category '" + entry.getCategoryId() + "'");
                }

                if (book.getEntry(entry.getId()) != null) {
                    return ValidationResult.failure("Cannot add runtime category '" + addCategory.category().getId() + "' because contained entry '" + entry.getId() + "' already exists in book '" + book.getId() + "'");
                }
            }

            return ValidationResult.success();
        }

        if (operation instanceof AddEntryOperation addEntry) {
            var category = book.getCategory(addEntry.categoryId());
            if (category == null) {
                return ValidationResult.failure("Cannot add runtime entry '" + addEntry.entry().getId() + "' because category '" + addEntry.categoryId() + "' does not exist in book '" + book.getId() + "'");
            }

            if (!addEntry.categoryId().equals(addEntry.entry().getCategoryId())) {
                return ValidationResult.failure("Cannot add runtime entry '" + addEntry.entry().getId() + "' because it targets category '" + addEntry.entry().getCategoryId() + "' instead of '" + addEntry.categoryId() + "'");
            }

            if (book.getEntry(addEntry.entry().getId()) != null || category.getEntry(addEntry.entry().getId()) != null) {
                return ValidationResult.failure("Cannot add runtime entry '" + addEntry.entry().getId() + "' because it already exists in book '" + book.getId() + "'");
            }

            return ValidationResult.success();
        }

        if (operation instanceof AddPageOperation addPage) {
            var category = book.getCategory(addPage.categoryId());
            if (category == null) {
                return ValidationResult.failure("Cannot add runtime page because category '" + addPage.categoryId() + "' does not exist in book '" + book.getId() + "'");
            }

            var entry = category.getEntry(addPage.entryId());
            if (entry == null) {
                return ValidationResult.failure("Cannot add runtime page because entry '" + addPage.entryId() + "' does not exist in category '" + addPage.categoryId() + "'");
            }

            if (!(entry instanceof BookContentEntry)) {
                return ValidationResult.failure("Cannot add runtime page to entry '" + addPage.entryId() + "' because only content entries support runtime pages");
            }

            return ValidationResult.success();
        }

        return ValidationResult.pendingResult();
    }

    private void applyResolvedOperation(Book book, Operation operation) {
        if (operation instanceof AddCategoryOperation addCategory) {
            book.addCategory(addCategory.category());
            for (var entry : addCategory.entries()) {
                addCategory.category().addEntry(entry);
            }
            return;
        }

        if (operation instanceof AddEntryOperation addEntry) {
            var category = book.getCategory(addEntry.categoryId());
            if (category != null) {
                category.addEntry(addEntry.entry());
            }
            return;
        }

        if (operation instanceof AddPageOperation addPage) {
            var category = book.getCategory(addPage.categoryId());
            if (category == null) {
                return;
            }

            var entry = category.getEntry(addPage.entryId());
            if (entry instanceof BookContentEntry contentEntry) {
                contentEntry.addPage(addPage.page());
            }
        }
    }

    private Operation cloneOperation(Operation operation) {
        if (operation instanceof AddCategoryOperation addCategory) {
            var category = this.cloneCategory(addCategory.category());
            var entries = addCategory.entries().stream().map(this::cloneEntry).toList();
            return new AddCategoryOperation(addCategory.bookId(), category, entries);
        }

        if (operation instanceof AddEntryOperation addEntry) {
            return new AddEntryOperation(addEntry.bookId(), addEntry.categoryId(), this.cloneEntry(addEntry.entry()));
        }

        if (operation instanceof AddPageOperation addPage) {
            return new AddPageOperation(addPage.bookId(), addPage.categoryId(), addPage.entryId(), this.clonePage(addPage.page()));
        }

        throw new IllegalStateException("Unknown runtime book content operation: " + operation.getClass().getName());
    }

    private BookCategory cloneCategory(BookCategory category) {
        var registryAccess = BookDataManager.get().registryAccess();
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        try {
            buffer.writeIdentifier(category.getId());
            category.toNetwork(buffer);
            buffer.readerIndex(0);
            return BookCategory.fromNetwork(buffer.readIdentifier(), buffer);
        } finally {
            buffer.release();
        }
    }

    private BookEntry cloneEntry(BookEntry entry) {
        var registryAccess = BookDataManager.get().registryAccess();
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        try {
            BookEntry.toNetwork(entry, buffer);
            buffer.readerIndex(0);
            return BookEntry.fromNetwork(buffer);
        } finally {
            buffer.release();
        }
    }

    private BookPage clonePage(BookPage page) {
        var registryAccess = BookDataManager.get().registryAccess();
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        try {
            BookPage.toNetwork(page, buffer);
            buffer.readerIndex(0);
            return BookPage.fromNetwork(buffer);
        } finally {
            buffer.release();
        }
    }

    private void reportError(Identifier bookId, String message) {
        BookErrorManager.get().setCurrentBookId(bookId);
        BookErrorManager.get().error(message);
        BookErrorManager.get().setCurrentBookId(null);
    }

    public interface Operation {
        Identifier bookId();
    }

    public record AddCategoryOperation(Identifier bookId, BookCategory category, List<BookEntry> entries) implements Operation {
        public AddCategoryOperation(Identifier bookId, BookCategory category) {
            this(bookId, category, List.copyOf(category.getEntries().values()));
        }
    }

    public record AddEntryOperation(Identifier bookId, Identifier categoryId, BookEntry entry) implements Operation {
    }

    public record AddPageOperation(Identifier bookId, Identifier categoryId, Identifier entryId, BookPage page) implements Operation {
    }

    private static final class StoredOperation {
        private final Operation operation;
        private int appliedGeneration = -1;
        private boolean discarded;

        private StoredOperation(Operation operation) {
            this.operation = operation;
        }
    }

    private enum ApplyResult {
        APPLIED,
        PENDING,
        FAILED
    }

    private record ValidationResult(boolean pending, String message) {
        private boolean isSuccess() {
            return !this.pending && this.message == null;
        }

        private boolean isPending() {
            return this.pending;
        }

        private static ValidationResult success() {
            return new ValidationResult(false, null);
        }

        private static ValidationResult pendingResult() {
            return new ValidationResult(true, null);
        }

        private static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }
}
