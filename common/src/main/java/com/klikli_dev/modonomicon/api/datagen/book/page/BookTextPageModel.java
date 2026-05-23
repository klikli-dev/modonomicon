/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookTextPageModel extends BookPageModel<BookTextPageModel> {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected boolean useMarkdownInTitle = false;
    protected boolean showTitleSeparator = true;
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookTextPageModel() {
        super(BookTextPage.ID);
    }

    public static BookTextPageModel create() {
        return new BookTextPageModel();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public boolean useMarkdownInTitle() {
        return this.useMarkdownInTitle;
    }

    public boolean showTitleSeparator() {
        return this.showTitleSeparator;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookTextPage(this.title.toBookTextHolder(), this.text.toBookTextHolder(), this.useMarkdownInTitle, this.showTitleSeparator, this.anchor, this.condition(provider));
    }

    public BookTextPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookTextPageModel withTitle(Component title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookTextPageModel withUseMarkdownInTitle(boolean useMarkdownInTitle) {
        this.useMarkdownInTitle = useMarkdownInTitle;
        return this;
    }

    public BookTextPageModel withShowTitleSeparator(boolean showTitleSeparator) {
        this.showTitleSeparator = showTitleSeparator;
        return this;
    }

    public BookTextPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookTextPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }
}
