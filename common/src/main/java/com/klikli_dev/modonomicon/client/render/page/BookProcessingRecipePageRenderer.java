/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookProcessingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;

public abstract class BookProcessingRecipePageRenderer<T extends Recipe<?>> extends BookRecipePageRenderer<T, BookProcessingRecipePage<T>> {
    public BookProcessingRecipePageRenderer(BookProcessingRecipePage<T> page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 45;
    }

    @Override
    protected void drawRecipe(GuiGraphicsExtractor guiGraphics, RecipeDisplayEntry recipeDisplayEntry, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

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

        var background = this.page.getBook().theme().content().processingRecipeBackground();
        background.extractRenderState(guiGraphics, recipeX, recipeY);
        //noinspection DataFlowIssue
        var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, recipeDisplayEntry.craftingRequirements().map(List::getFirst).orElseThrow());
        this.parentScreen.renderItemStacks(guiGraphics, recipeX + 40, recipeY + 4, mouseX, mouseY, recipeDisplayEntry.display().craftingStation().resolveForStacks(context));
        this.parentScreen.renderItemStacks(guiGraphics, recipeX + 76, recipeY + 4, mouseX, mouseY, recipeDisplayEntry.display().result().resolveForStacks(context));
    }
}

