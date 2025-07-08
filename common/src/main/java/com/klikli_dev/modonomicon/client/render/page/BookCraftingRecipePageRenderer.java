/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.page.BookCraftingRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;

public class BookCraftingRecipePageRenderer extends BookRecipePageRenderer<Recipe<?>, BookCraftingRecipePage> {
    public BookCraftingRecipePageRenderer(BookCraftingRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 75;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeDisplayEntry recipeDisplayEntry, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {

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

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getBook().getCraftingTexture(), recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);


        boolean isShapeless = recipeDisplayEntry.display() instanceof ShapelessCraftingRecipeDisplay;
        if (isShapeless) {
            int iconX = recipeX + 62;
            int iconY = recipeY + 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getBook().getCraftingTexture(), iconX, iconY, 0, 64, 11, 11, 128, 256);
            if (this.parentScreen.isMouseInRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
                this.parentScreen.setTooltip(Component.translatable(Tooltips.RECIPE_CRAFTING_SHAPELESS));
            }
        }

        //noinspection DataFlowIssue
        var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);

        this.parentScreen.renderItemStacks(guiGraphics, recipeX + 79, recipeY + 22, mouseX, mouseY, recipeDisplayEntry.resultItems(context));


//        var ingredients = recipeDisplayEntry.craftingRequirements().orElse(List.of());
//
//        int wrap = 3;
//        if (recipeDisplayEntry.display() instanceof ShapedCraftingRecipeDisplay shaped) {
//            wrap = shaped.width();
//        }

//
//        for (int i = 0; i < ingredients.size(); i++) {
//            this.parentScreen.renderIngredient(guiGraphics, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
//        }

        switch (recipeDisplayEntry.display()) {
            case ShapedCraftingRecipeDisplay shaped:
                PlaceRecipeHelper.placeRecipe(
                        3, //we only support 3x3 grid size recipes
                        3,
                        shaped.width(),
                        shaped.height(),
                        shaped.ingredients(),
                        (item, slot, x, y) -> {
                            this.parentScreen.renderItemStacks(guiGraphics, recipeX + x * 19 + 3, recipeY + y * 19 + 3, mouseX, mouseY, item.resolveForStacks(context));
                        }
                );
                break;
            case ShapelessCraftingRecipeDisplay shapeless:
                PlaceRecipeHelper.placeRecipe(
                        3, //we only support 3x3 grid size recipes
                        3,
                        3,
                        3,
                        shapeless.ingredients(),
                        (item, slot, x, y) -> {
                            this.parentScreen.renderItemStacks(guiGraphics, recipeX + x * 19 + 3, recipeY + y * 19 + 3, mouseX, mouseY, item.resolveForStacks(context));
                        }
                );
            default:
        }

        this.parentScreen.renderItemStacks(guiGraphics, recipeX + 79, recipeY + 41, mouseX, mouseY, recipeDisplayEntry.display().craftingStation().resolveForStacks(context));
    }
}
