/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BookRecipePageModel extends BookPageModel {

    protected BookTextHolderModel title1 = new BookTextHolderModel("");
    protected String recipeId1 = "";

    protected BookTextHolderModel title2 = new BookTextHolderModel("");
    protected String recipeId2 = "";

    protected BookTextHolderModel text = new BookTextHolderModel("");


    protected BookRecipePageModel(ResourceLocation type, @NotNull String anchor) {
        super(type, anchor);
    }

    public BookTextHolderModel getTitle1() {
        return this.title1;
    }

    public String getRecipeId1() {
        return this.recipeId1;
    }

    public BookTextHolderModel getTitle2() {
        return this.title2;
    }

    public String getRecipeId2() {
        return this.recipeId2;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.add("title1", this.title1.toJson());
        if (this.recipeId1 != null && !this.recipeId1.isEmpty()) {
            json.addProperty("recipe_id_1", this.recipeId1);
        }
        json.add("title2", this.title2.toJson());
        if (this.recipeId2 != null && !this.recipeId2.isEmpty()) {
            json.addProperty("recipe_id_2", this.recipeId2);
        }
        json.add("text", this.text.toJson());
        return json;
    }


    public static abstract class Builder<T extends Builder<T>> {
        protected BookTextHolderModel title1 = new BookTextHolderModel("");
        protected String recipeId1 = "";

        protected BookTextHolderModel title2 = new BookTextHolderModel("");
        protected String recipeId2 = "";

        protected BookTextHolderModel text = new BookTextHolderModel("");

        protected String anchor = "";

        protected Builder() {
        }

        public abstract T getThis();

        public T withAnchor(String anchor) {
            this.anchor = anchor;
            return this.getThis();
        }

        public T withTitle1(String title) {
            this.title1 = new BookTextHolderModel(title);
            return this.getThis();
        }

        public T withTitle1(Component title) {
            this.title1 = new BookTextHolderModel(title);
            return this.getThis();
        }

        public T withTitle2(String title) {
            this.title2 = new BookTextHolderModel(title);
            return this.getThis();
        }

        public T withTitle2(Component title) {
            this.title2 = new BookTextHolderModel(title);
            return this.getThis();
        }

        public T withText(String text) {
            this.text = new BookTextHolderModel(text);
            return this.getThis();
        }

        public T withText(Component text) {
            this.text = new BookTextHolderModel(text);
            return this.getThis();
        }

        public T withRecipeId1(String recipeId) {
            this.recipeId1 = recipeId;
            return this.getThis();
        }

        public T withRecipeId1(ResourceLocation recipeId) {
            this.recipeId1 = recipeId.toString();
            return this.getThis();
        }

        public T withRecipeId2(String recipeId) {
            this.recipeId2 = recipeId;
            return this.getThis();
        }

        public T withRecipeId2(ResourceLocation recipeId) {
            this.recipeId2 = recipeId.toString();
            return this.getThis();
        }
    }
}
