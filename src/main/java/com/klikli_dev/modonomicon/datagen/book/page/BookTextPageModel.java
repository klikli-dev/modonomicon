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

package com.klikli_dev.modonomicon.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.datagen.book.BookTextHolderModel;
import org.jetbrains.annotations.NotNull;

public class BookTextPageModel extends BookPageModel {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected boolean useMarkdownInTitle = false;
    protected boolean showTitleSeparator = true;
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookTextPageModel(@NotNull String anchor) {
        super(Page.TEXT, anchor);
    }

    public static Builder builder() {
        return new Builder();
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
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("title", this.title.toJson());
        json.addProperty("use_markdown_in_title", this.useMarkdownInTitle);
        json.addProperty("show_title_separator", this.showTitleSeparator);
        json.add("text", this.text.toJson());
        return json;
    }


    public static final class Builder {
        protected String anchor = "";
        protected BookTextHolderModel title = new BookTextHolderModel("");
        protected boolean useMarkdownInTitle = false;
        protected boolean showTitleSeparator = true;
        protected BookTextHolderModel text = new BookTextHolderModel("");

        private Builder() {
        }

        public static Builder aBookTextPageModel() {
            return new Builder();
        }


        public Builder withAnchor(String anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder withTitle(BookTextHolderModel title) {
            this.title = title;
            return this;
        }

        public Builder withUseMarkdownInTitle(boolean useMarkdownInTitle) {
            this.useMarkdownInTitle = useMarkdownInTitle;
            return this;
        }

        public Builder withShowTitleSeparator(boolean showTitleSeparator) {
            this.showTitleSeparator = showTitleSeparator;
            return this;
        }

        public Builder withText(BookTextHolderModel text) {
            this.text = text;
            return this;
        }

        public BookTextPageModel build() {
            BookTextPageModel bookTextPageModel = new BookTextPageModel(this.anchor);
            bookTextPageModel.showTitleSeparator = this.showTitleSeparator;
            bookTextPageModel.useMarkdownInTitle = this.useMarkdownInTitle;
            bookTextPageModel.title = this.title;
            bookTextPageModel.text = this.text;
            return bookTextPageModel;
        }
    }
}
