/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BookTextPageModel extends BookPageModel {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected boolean useMarkdownInTitle = false;
    protected boolean showTitleSeparator = true;
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookTextPageModel(@NotNull String anchor, @NotNull BookConditionModel condition) {
        super(Page.TEXT, anchor, condition);
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
        private String anchor = "";
        private BookConditionModel condition = new BookNoneConditionModel();
        private BookTextHolderModel title = new BookTextHolderModel("");
        private boolean useMarkdownInTitle = false;
        private boolean showTitleSeparator = true;
        private BookTextHolderModel text = new BookTextHolderModel("");

        private Builder() {
        }

        public static Builder aBookTextPageModel() {
            return new Builder();
        }


        public Builder withAnchor(String anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder withCondition(BookConditionModel condition) {
            this.condition = condition;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = new BookTextHolderModel(title);
            return this;
        }

        public Builder withTitle(Component title) {
            this.title = new BookTextHolderModel(title);
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

        public Builder withText(String text) {
            this.text = new BookTextHolderModel(text);
            return this;
        }

        public Builder withText(Component text) {
            this.text = new BookTextHolderModel(text);
            return this;
        }

        public BookTextPageModel build() {
            BookTextPageModel bookTextPageModel = new BookTextPageModel(this.anchor, this.condition);
            bookTextPageModel.showTitleSeparator = this.showTitleSeparator;
            bookTextPageModel.useMarkdownInTitle = this.useMarkdownInTitle;
            bookTextPageModel.title = this.title;
            bookTextPageModel.text = this.text;
            return bookTextPageModel;
        }
    }
}
