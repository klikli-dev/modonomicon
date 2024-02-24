/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookImagePageModel extends BookPageModel {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected ResourceLocation[] images = new ResourceLocation[0];
    protected boolean border = true;

    protected BookImagePageModel(@NotNull String anchor, @NotNull BookConditionModel condition) {
        super(Page.IMAGE, anchor, condition);
    }

    public static Builder builder() {
        return new Builder();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public ResourceLocation[] getImages() {
        return this.images;
    }

    public boolean isBorder() {
        return this.border;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("title", this.title.toJson());
        json.add("text", this.text.toJson());
        json.addProperty("border", this.border);

        var imagesArray = new JsonArray();
        for (int i = 0; i < this.images.length; i++) {
            imagesArray.add(this.images[i].toString());
        }
        json.add("images", imagesArray);

        return json;
    }


    public static final class Builder {
        private String anchor = "";
        private BookConditionModel condition = new BookNoneConditionModel();
        private BookTextHolderModel title = new BookTextHolderModel("");
        private BookTextHolderModel text = new BookTextHolderModel("");
        private ResourceLocation[] images = new ResourceLocation[0];
        private boolean border = true;

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

        public Builder withBorder(boolean border) {
            this.border = border;
            return this;
        }

        public Builder withImages(ResourceLocation... images) {
            this.images = images;
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

        public BookImagePageModel build() {
            BookImagePageModel model = new BookImagePageModel(this.anchor, this.condition);
            model.title = this.title;
            model.text = this.text;
            model.images = this.images;
            model.border = this.border;
            return model;
        }
    }
}
