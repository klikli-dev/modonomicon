/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookViewerRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.UnaryOperator;

public class BookViewerRecipePageModel extends BookRecipePageModel<BookViewerRecipePageModel> {

    /**
     * Attributes for the first recipe, e.g. the scale it is rendered at.
     */
    protected RecipeAttributeModel recipe1 = RecipeAttributeModel.create();

    /**
     * Attributes for the second recipe, e.g. the scale it is rendered at.
     */
    protected RecipeAttributeModel recipe2 = RecipeAttributeModel.create();

    protected BookViewerRecipePageModel() {
        super(BookViewerRecipePage.ID);
    }

    public static BookViewerRecipePageModel create() {
        return new BookViewerRecipePageModel();
    }

    public RecipeAttributeModel getRecipe1() {
        return this.recipe1;
    }

    public RecipeAttributeModel getRecipe2() {
        return this.recipe2;
    }

    /**
     * Configures the attributes of the first recipe, e.g. {@code withRecipe1(r -> r.withScale(0.5f))}.
     */
    public BookViewerRecipePageModel withRecipe1(UnaryOperator<RecipeAttributeModel> operator) {
        this.recipe1 = operator.apply(this.recipe1);
        return this;
    }

    /**
     * Configures the attributes of the second recipe, e.g. {@code withRecipe2(r -> r.withScale(0.5f))}.
     */
    public BookViewerRecipePageModel withRecipe2(UnaryOperator<RecipeAttributeModel> operator) {
        this.recipe2 = operator.apply(this.recipe2);
        return this;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookViewerRecipePage(
                new BookRecipePage.JsonDataHolder(
                        this.title1.toBookTextHolder(),
                        this.recipeId1 == null || this.recipeId1.isEmpty() ? null : ResourceKey.create(Registries.RECIPE, Identifier.parse(this.recipeId1)),
                        this.title2.toBookTextHolder(),
                        this.recipeId2 == null || this.recipeId2.isEmpty() ? null : ResourceKey.create(Registries.RECIPE, Identifier.parse(this.recipeId2)),
                        this.text.toBookTextHolder(),
                        this.id,
                        this.condition(provider)
                ),
                this.recipe1.toRecipeAttribute(),
                this.recipe2.toRecipeAttribute()
        );
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder common) {
        return new BookViewerRecipePage(common);
    }
}
