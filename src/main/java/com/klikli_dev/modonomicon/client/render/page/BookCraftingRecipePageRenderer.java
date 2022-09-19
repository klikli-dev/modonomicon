/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.page.BookCraftingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class BookCraftingRecipePageRenderer extends BookRecipePageRenderer<Recipe<?>, BookCraftingRecipePage> {
    public BookCraftingRecipePageRenderer(BookCraftingRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(PoseStack poseStack, Recipe<?> recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

        if(!second){
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(this.page.getTitle1(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, -5);
            }
        }
        else {
            if (!this.page.getTitle2().isEmpty()) {
                this.renderTitle(this.page.getTitle2(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2,
                        recipeY - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
            }
        }

        RenderSystem.setShaderTexture(0, this.page.getBook().getCraftingTexture());
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

        boolean shaped = recipe instanceof ShapedRecipe;
        if (!shaped) {
            int iconX = recipeX + 62;
            int iconY = recipeY + 2;
            GuiComponent.blit(poseStack, iconX, iconY, 0, 64, 11, 11, 128, 256);
            if (this.parentScreen.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
                this.parentScreen.setTooltip(new TranslatableComponent(Tooltips.RECIPE_CRAFTING_SHAPELESS));
            }
        }

        this.parentScreen.renderItemStack(poseStack, recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getResultItem());

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int wrap = 3;
        if (shaped) {
            wrap = ((ShapedRecipe) recipe).getWidth();
        }

        for (int i = 0; i < ingredients.size(); i++) {
            this.parentScreen.renderIngredient(poseStack, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
        }

        this.parentScreen.renderItemStack(poseStack, recipeX + 79, recipeY + 41, mouseX, mouseY, recipe.getToastSymbol());
    }
}
