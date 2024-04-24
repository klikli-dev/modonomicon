/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

public class BookTextPageModel extends BookPageModel<BookTextPageModel> {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected boolean useMarkdownInTitle = false;
    protected boolean showTitleSeparator = true;
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookTextPageModel() {
        super(Page.TEXT);
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
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.add("title", this.title.toJson(provider));
        json.addProperty("use_markdown_in_title", this.useMarkdownInTitle);
        json.addProperty("show_title_separator", this.showTitleSeparator);
        json.add("text", this.text.toJson(provider));
        return json;
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
