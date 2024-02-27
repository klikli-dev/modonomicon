/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import org.jetbrains.annotations.NotNull;

public class BookSmeltingRecipePageModel extends BookRecipePageModel {
    protected BookSmeltingRecipePageModel(@NotNull String anchor, @NotNull BookConditionModel condition) {
        super(Page.SMELTING_RECIPE, anchor, condition);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BookRecipePageModel.Builder<Builder> {
        protected Builder() {
            super();
        }

        public BookSmeltingRecipePageModel build() {
            BookSmeltingRecipePageModel model = new BookSmeltingRecipePageModel(this.anchor, this.condition);
            model.title1 = this.title1;
            model.recipeId1 = this.recipeId1;
            model.title2 = this.title2;
            model.recipeId2 = this.recipeId2;
            model.text = this.text;
            return model;
        }

        @Override
        public Builder getThis() {
            return this;
        }
    }
}
