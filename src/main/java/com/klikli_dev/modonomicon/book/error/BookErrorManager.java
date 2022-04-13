/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.book.error;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BookErrorManager {
    private static final BookErrorManager instance = new BookErrorManager();

    private final Map<ResourceLocation, BookErrorHolder> booksErrors = new HashMap<>();

    private ResourceLocation currentBookId;

    private BookErrorManager() {

    }

    public static BookErrorManager get() {
        return instance;
    }

    public BookErrorHolder getErrors(ResourceLocation bookId) {
        return this.booksErrors.get(bookId);
    }

    public boolean hasErrors(ResourceLocation book) {
        var holder = this.booksErrors.get(book);
        return holder != null && !holder.getErrors().isEmpty();
    }

    public void error(String message) {
        this.error(new BookErrorInfo(message, null));
    }

    public void error(String message, Exception exception) {
        this.error(new BookErrorInfo(message, exception));
    }

    public void error(BookErrorInfo error) {
        this.error(this.currentBookId, error);
    }

    public void error(ResourceLocation book, String message) {
        this.error(book, new BookErrorInfo(message, null));
    }

    public void error(ResourceLocation book, String message, Exception exception) {
        this.error(book, new BookErrorInfo(message, exception));
    }

    public void error(ResourceLocation book, BookErrorInfo error) {
        if(book == null) {
            Modonomicon.LOGGER.error("BookErrorManager.error() called with null book id with error: {}", error);
            return;
        }

        var holder = this.booksErrors.get(book);
        if (holder == null) {
            holder = new BookErrorHolder();
            this.booksErrors.put(book, holder);
        }
        holder.addError(error);

        Modonomicon.LOGGER.warn("BookErrorManager.error() called for book: {} with error: {}", book, error);
    }

    /**
     * Gets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public ResourceLocation getCurrentBookId() {
        return this.currentBookId;
    }

    /**
     * Sets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public ResourceLocation setCurrentBookId(ResourceLocation id) {
        return this.currentBookId = id;
    }
}
