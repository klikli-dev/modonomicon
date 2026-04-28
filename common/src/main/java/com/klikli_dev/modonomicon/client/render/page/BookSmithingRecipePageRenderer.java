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
import net.minecraft.client.renderer.RenderPipelines;

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

        var background = this.page.getBook().theme().content().smithingRecipeBackground();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, background.texture(), recipeX, recipeY, 0, 0, background.width(), background.height(), background.width(), background.height());

        if (recipeDisplayEntry.display() instanceof SmithingRecipeDisplay(
                net.minecraft.world.item.crafting.display.SlotDisplay template,
                net.minecraft.world.item.crafting.display.SlotDisplay base,
                net.minecraft.world.item.crafting.display.SlotDisplay addition,
                net.minecraft.world.item.crafting.display.SlotDisplay result,
                net.minecraft.world.item.crafting.display.SlotDisplay craftingStation
        )) {
            //noinspection DataFlowIssue
            var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, template.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 23, mouseX, mouseY, base.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 4, recipeY + 42, mouseX, mouseY, addition.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 40, recipeY + 23, mouseX, mouseY, craftingStation.resolveForStacks(context));
            this.parentScreen.renderItemStacks(guiGraphics, recipeX + 76, recipeY + 23, mouseX, mouseY, result.resolveForStacks(context));
        }
    }
}
