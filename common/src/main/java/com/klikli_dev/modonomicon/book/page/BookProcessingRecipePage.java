/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.book.BookTextHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class BookProcessingRecipePage<T extends Recipe<?>> extends BookRecipePage<T> {
    public BookProcessingRecipePage(RecipeType<T> recipeType, BookTextHolder title1, ResourceLocation recipeId1, BookTextHolder title2, ResourceLocation recipeId2, BookTextHolder text, String anchor) {
        super(recipeType, title1, recipeId1, title2, recipeId2, text, anchor);
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, T recipe) {
        if (recipe == null) {
            return ItemStack.EMPTY;
        }

        return recipe.getResultItem(level.registryAccess());
    }
}
