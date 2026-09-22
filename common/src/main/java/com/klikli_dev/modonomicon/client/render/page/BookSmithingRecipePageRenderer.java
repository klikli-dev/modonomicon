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
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
        return 75;
    }

    @Override
    protected void drawRecipe(GuiGraphicsExtractor guiGraphics, RecipeDisplayEntry recipeDisplayEntry, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

        if (!second) {
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle1(), false, BookEntryScreen.PAGE_WIDTH / 2, -5);
            }
        } else {
            if (!this.page.getTitle2().isEmpty()) {
                this.renderTitle(guiGraphics, this.page.getTitle2(), false, BookEntryScreen.PAGE_WIDTH / 2,
                        recipeY - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
            }
        }

        var background = this.page.getBook().theme().content().smithingRecipeBackground();
        background.extractRenderState(guiGraphics, recipeX, recipeY - 2);

        if (recipeDisplayEntry.display() instanceof SmithingRecipeDisplay(
                net.minecraft.world.item.crafting.display.SlotDisplay template,
                net.minecraft.world.item.crafting.display.SlotDisplay base,
                net.minecraft.world.item.crafting.display.SlotDisplay addition,
                net.minecraft.world.item.crafting.display.SlotDisplay result,
                net.minecraft.world.item.crafting.display.SlotDisplay craftingStation
        )) {
            //noinspection DataFlowIssue
            var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 2, mouseX, mouseY, template.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 21, mouseX, mouseY, base.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 40, mouseX, mouseY, addition.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 40, recipeY + 21, mouseX, mouseY, craftingStation.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 76, recipeY + 21, mouseX, mouseY, result.resolveForStacks(context));
        }
    }
}

