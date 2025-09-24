/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;

public class BookSmithingRecipePageRenderer extends BookRecipePageRenderer<SmithingRecipe, BookSmithingRecipePage> {
    public BookSmithingRecipePageRenderer(BookSmithingRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 76;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeDisplayEntry recipeDisplayEntry, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

        recipeY += 10;


        if (!second) {
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle1(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            }
        } else {
            if (!this.page.getTitle2().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle2(), false, BookEntryScreen.PAGE_WIDTH / 2,
                        recipeY - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
            }
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getBook().getCraftingTexture(), recipeX, recipeY, 11, 178, 96, 62, 128, 256);

        if (recipeDisplayEntry.display() instanceof SmithingRecipeDisplay smithingRecipeDisplay) {
            //noinspection DataFlowIssue
            var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, smithingRecipeDisplay.template().resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 23, mouseX, mouseY, smithingRecipeDisplay.base().resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 42, mouseX, mouseY, smithingRecipeDisplay.addition().resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 40, recipeY + 23, mouseX, mouseY, smithingRecipeDisplay.craftingStation().resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 76, recipeY + 23, mouseX, mouseY, smithingRecipeDisplay.result().resolveForStacks(context));
        }
    }
}
