/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public abstract class BookRecipePageModel<T extends BookRecipePageModel<T>> extends BookPageModel<T> {

    protected BookTextHolderModel title1 = new BookTextHolderModel("");
    protected String recipeId1 = "";

    protected BookTextHolderModel title2 = new BookTextHolderModel("");
    protected String recipeId2 = "";

    protected BookTextHolderModel text = new BookTextHolderModel("");


    protected BookRecipePageModel(Identifier type) {
        super(type);
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
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return this.createPage(new BookRecipePage.JsonDataHolder(
                this.title1.toBookTextHolder(),
                this.recipeId1 == null || this.recipeId1.isEmpty() ? null : ResourceKey.create(Registries.RECIPE, Identifier.parse(this.recipeId1)),
                this.title2.toBookTextHolder(),
                this.recipeId2 == null || this.recipeId2.isEmpty() ? null : ResourceKey.create(Registries.RECIPE, Identifier.parse(this.recipeId2)),
                this.text.toBookTextHolder(),
                this.id,
                this.condition(provider)
        ));
    }

    protected abstract BookPage createPage(BookRecipePage.JsonDataHolder common);

    public T withTitle1(String title) {
        this.title1 = new BookTextHolderModel(title);
        //noinspection unchecked
        return (T) this;
    }

    public T withTitle1(Component title) {
        this.title1 = new BookTextHolderModel(title);
        //noinspection unchecked
        return (T) this;
    }

    public T withTitle2(String title) {
        this.title2 = new BookTextHolderModel(title);
        //noinspection unchecked
        return (T) this;
    }

    public T withTitle2(Component title) {
        this.title2 = new BookTextHolderModel(title);
        //noinspection unchecked
        return (T) this;
    }

    public T withText(String text) {
        this.text = new BookTextHolderModel(text);
        //noinspection unchecked
        return (T) this;
    }

    public T withText(Component text) {
        this.text = new BookTextHolderModel(text);
        //noinspection unchecked
        return (T) this;
    }

    public T withRecipeId1(String recipeId) {
        this.recipeId1 = recipeId;
        //noinspection unchecked
        return (T) this;
    }

    public T withRecipeId1(Identifier recipeId) {
        this.recipeId1 = recipeId.toString();
        //noinspection unchecked
        return (T) this;
    }

    public T withRecipeId2(String recipeId) {
        this.recipeId2 = recipeId;
        //noinspection unchecked
        return (T) this;
    }

    public T withRecipeId2(Identifier recipeId) {
        this.recipeId2 = recipeId.toString();
        //noinspection unchecked
        return (T) this;
    }
}
