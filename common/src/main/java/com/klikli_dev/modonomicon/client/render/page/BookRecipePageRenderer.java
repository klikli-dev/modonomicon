/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import org.jetbrains.annotations.Nullable;

public abstract class BookRecipePageRenderer<R extends Recipe<?>, T extends BookRecipePage<R>> extends BookPageRenderer<T> implements PageWithTextRenderer {

    public static int Y = 4;
    public static int X = BookEntryScreen.PAGE_WIDTH / 2 - 49;

    public BookRecipePageRenderer(T page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        int recipeX = X;
        int recipeY = Y;

        if (this.page.getRecipeDisplayEntry1() != null) {

            //Title 1 is always rendered (falls back to recipe name)
            this.drawRecipe(guiGraphics, this.page.getRecipeDisplayEntry1(), recipeX, recipeY, mouseX, mouseY, false);


            if (this.page.getRecipeDisplayEntry2() != null) {
                //Title 2 might be skipped if identical to Title 2, so respect that here
                this.drawRecipe(guiGraphics, this.page.getRecipeDisplayEntry2(), recipeX,
                        recipeY + this.getRecipeHeight() - (this.page.getTitle2().getString().isEmpty() ? 10 : 0),
                        mouseX, mouseY, true);
            }
        } else {
            this.drawWrappedStringNoShadow(guiGraphics,
                    Component.translatable(ModonomiconConstants.I18n.Gui.RECIPE_PAGE_RECIPE_MISSING, this.page.getRecipeKey1().toString()),
                    recipeX - 13, recipeY - 15, 0xFF0000, BookEntryScreen.PAGE_WIDTH);
        }

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);


        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {

            //titles are not markdown enabled here, so no links

            var x = this.parentScreen.getBook().getBookTextOffsetX();
            var y = this.getTextY() + this.parentScreen.getBook().getBookTextOffsetY();
            var width = BookEntryScreen.PAGE_WIDTH + this.parentScreen.getBook().getBookTextOffsetWidth() - x; //always remove the offset x from the width to avoid overflow
            var height = BookEntryScreen.PAGE_HEIGHT + this.parentScreen.getBook().getBookTextOffsetHeight() - y; //always remove the offset y from the height to avoid overflow

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), x, y, width, height, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        var y = Y + (this.getRecipeHeight() * (this.page.getRecipeDisplayEntry2() == null ? 1 : 2));
        y -= this.page.getTitle2().isEmpty() ? 10 : 0;
        y -= this.page.getRecipeDisplayEntry2() == null ? 0 : 10;
        return y;
    }

    protected abstract int getRecipeHeight();

    protected abstract void drawRecipe(GuiGraphics guiGraphics, RecipeDisplayEntry recipeDisplayEntry, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
}
