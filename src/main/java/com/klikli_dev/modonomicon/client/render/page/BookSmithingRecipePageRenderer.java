/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class BookSmithingRecipePageRenderer extends BookRecipePageRenderer<UpgradeRecipe, BookSmithingRecipePage> {
    public BookSmithingRecipePageRenderer(BookSmithingRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 60;
    }

    @Override
    protected void drawRecipe(PoseStack poseStack, UpgradeRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

        recipeY += 10;


        if(!second){
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(this.page.getTitle1(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);
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
        GuiComponent.blit(poseStack, recipeX, recipeY, 11, 135, 96, 43, 128, 256);

        this.parentScreen.renderIngredient(poseStack, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.base);
        this.parentScreen.renderIngredient(poseStack, recipeX + 4, recipeY + 23, mouseX, mouseY, recipe.addition);
        this.parentScreen.renderItemStack(poseStack, recipeX + 40, recipeY + 13, mouseX, mouseY, recipe.getToastSymbol());
        this.parentScreen.renderItemStack(poseStack, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getResultItem());
    }
}
