/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.book.Book;

public final class DefaultBookTheme implements BookTheme {

    public static final DefaultBookTheme INSTANCE = new DefaultBookTheme();

    private final BookSkinTheme skin;
    private final BookPresentationTheme presentation;

    private DefaultBookTheme() {
        this(DefaultBookSkinTheme.INSTANCE, DefaultBookPresentationTheme.INSTANCE);
    }

    private DefaultBookTheme(BookSkinTheme skin, BookPresentationTheme presentation) {
        this.skin = skin;
        this.presentation = presentation;
    }

    public static BookTheme forBook(Book book) {
        return new DefaultBookTheme(DefaultBookSkinTheme.INSTANCE, new BookBackedPresentationTheme(book));
    }

    @Override
    public BookSkinTheme skin() {
        return this.skin;
    }

    @Override
    public BookPresentationTheme presentation() {
        return this.presentation;
    }
}
