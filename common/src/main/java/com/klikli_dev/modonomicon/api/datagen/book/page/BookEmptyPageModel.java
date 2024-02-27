/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import org.jetbrains.annotations.NotNull;

public class BookEmptyPageModel extends BookPageModel {

    protected BookEmptyPageModel(@NotNull String anchor, @NotNull BookConditionModel condition) {
        super(Page.EMPTY, anchor, condition);
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
        private String anchor = "";
        private BookConditionModel condition = new BookNoneConditionModel();

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

        public BookEmptyPageModel build() {
            BookEmptyPageModel bookTextPageModel = new BookEmptyPageModel(this.anchor, this.condition);
            return bookTextPageModel;
        }
    }
}
