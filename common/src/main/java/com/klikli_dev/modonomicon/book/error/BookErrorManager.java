/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.error;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;

public class BookErrorManager {
    private static final BookErrorManager instance = new BookErrorManager();

    private final Map<Identifier, BookErrorHolder> booksErrors = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private final BookErrorContextHelper contextHelper = new BookErrorContextHelper();
    private Identifier currentBookId;
    private String currentContext;

    private BookErrorManager() {

    }

    public static BookErrorManager get() {
        return instance;
    }

    public void reset() {
        this.contextHelper.reset();
        this.booksErrors.clear();
        this.currentBookId = null;
        this.currentContext = null;
    }

    public BookErrorContextHelper getContextHelper() {
        return this.contextHelper;
    }

    public BookErrorHolder getErrors(Identifier bookId) {
        return this.booksErrors.get(bookId);
    }

    public boolean hasErrors(Identifier book) {
        var holder = this.booksErrors.get(book);
        return holder != null && !holder.getErrors().isEmpty();
    }

    public void error(String message) {
        this.error(new BookErrorInfo(message, null, this.currentContext));
    }

    public void error(String message, Exception exception) {
        this.error(new BookErrorInfo(message, exception, this.currentContext));
    }

    public void error(BookErrorInfo error) {
        this.error(this.currentBookId, error);
    }

    public void error(Identifier book, String message) {
        this.error(book, new BookErrorInfo(message, null, this.currentContext));
    }

    public void error(Identifier book, String message, Exception exception) {
        this.error(book, new BookErrorInfo(message, exception, this.currentContext));
    }

    public void error(Identifier book, BookErrorInfo error) {
        if (book == null) {
            Modonomicon.LOG.error("BookErrorManager.error() called with null book id with error: {}", error);
            return;
        }

        var holder = this.booksErrors.get(book);
        if (holder == null) {
            holder = new BookErrorHolder();
            this.booksErrors.put(book, holder);
        }
        holder.addError(error);

        Modonomicon.LOG.warn("BookErrorManager.error() called for book: {} with error: {}", book, error);
    }

    /**
     * Gets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public Identifier getCurrentBookId() {
        return this.currentBookId;
    }

    /**
     * Sets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public void setCurrentBookId(Identifier id) {
        this.currentBookId = id;
    }

    public void setTo(BookPage page) {
        this.setTo(page.getParentEntry());
        BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
    }

    public void setTo(BookContentEntry entry) {
        BookErrorManager.get().setCurrentBookId(entry.getBook().getId());
        BookErrorManager.get().getContextHelper().reset();
        BookErrorManager.get().getContextHelper().categoryId = entry.getCategoryId();
        BookErrorManager.get().getContextHelper().entryId = entry.getId();
    }

    /**
     * Set the context to add to all errors logged after this. Set to null to remove context. Uses
     * {@link MessageFormatter#format(String, Object)} to format the context.
     */
    public void setContext(String context, Object... args) {
        if (context != null) {
            this.currentContext = MessageFormatter.arrayFormat(context, args).getMessage();
        } else {
            this.currentContext = null;
        }
    }

    public String getContext() {
        if (this.currentContext != null) {
            return this.currentContext;
        }

        return this.contextHelper.toString();
    }
}
