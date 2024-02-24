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
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class BookSpotlightPageModel extends BookPageModel {
    protected Ingredient item = Ingredient.EMPTY;
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookSpotlightPageModel(@NotNull String anchor, @NotNull BookConditionModel condition) {
        super(Page.SPOTLIGHT, anchor, condition);
    }

    public static Builder builder() {
        return new Builder();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public Ingredient getItem() {
        return this.item;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("title", this.title.toJson());
        json.add("item", this.item.toJson());
        json.add("text", this.text.toJson());
        return json;
    }


    public static final class Builder {
        private String anchor = "";
        private BookConditionModel condition = new BookNoneConditionModel();
        private BookTextHolderModel title = new BookTextHolderModel("");
        private Ingredient item = Ingredient.EMPTY;
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

        public Builder withItem(Ingredient item) {
            this.item = item;
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

        public BookSpotlightPageModel build() {
            BookSpotlightPageModel model = new BookSpotlightPageModel(this.anchor, this.condition);
            model.item = this.item;
            model.title = this.title;
            model.text = this.text;
            return model;
        }
    }
}
