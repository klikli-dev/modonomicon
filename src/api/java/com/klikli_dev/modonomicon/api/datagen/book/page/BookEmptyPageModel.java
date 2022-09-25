/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import org.jetbrains.annotations.NotNull;

public class BookEmptyPageModel extends BookPageModel {

    protected BookEmptyPageModel(@NotNull String anchor) {
        super(Page.EMPTY, anchor);
    }

    public static Builder builder() {
        return new Builder();
    }


    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        return json;
    }


    public static final class Builder {
        protected String anchor = "";

        private Builder() {
        }

        public static Builder aBookTextPageModel() {
            return new Builder();
        }


        public Builder withAnchor(String anchor) {
            this.anchor = anchor;
            return this;
        }

        public BookEmptyPageModel build() {
            BookEmptyPageModel bookTextPageModel = new BookEmptyPageModel(this.anchor);
            return bookTextPageModel;
        }
    }
}
