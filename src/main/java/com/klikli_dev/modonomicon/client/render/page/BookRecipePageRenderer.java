/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public abstract class BookRecipePageRenderer<R extends Recipe<?>, T extends BookRecipePage<R>>  extends BookPageRenderer<T> implements PageWithTextRenderer {

    public static int Y = 4;
    public static int X = BookContentScreen.PAGE_WIDTH / 2 - 49;

    public BookRecipePageRenderer(T page) {
        super(page);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        int recipeX = X;
        int recipeY = Y;

        if (this.page.getRecipe1() != null) {

            //Title 1 is always rendered (falls back to recipe name)
            drawRecipe(poseStack, this.page.getRecipe1(), recipeX, recipeY, mouseX, mouseY, false);


            if (this.page.getRecipe2()  != null) {
                //Title 2 might be skipped if identical to Title 2, so respect that here
                drawRecipe(poseStack, this.page.getRecipe2(), recipeX,
                        recipeY + getRecipeHeight() - (this.page.getTitle2().getString().isEmpty() ? 10 : 0),
                        mouseX, mouseY, true);
            }
        }

        if (!this.page.getTitle1().isEmpty()) {
            this.renderTitle(this.page.getTitle1(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, - 10);
        }

        if (!this.page.getTitle2().isEmpty()) {
            this.renderTitle(this.page.getTitle2(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2,
                    recipeY + getRecipeHeight() - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
        }

        this.renderBookTextHolder(this.getPage().getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {

            //titles are not markdown enabled here, so no links

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return Y + this.getRecipeHeight()  * (this.page.getRecipe2() == null ? 1 : 2) - (this.page.getTitle2().isEmpty() ? 26 : 16);
    }

    protected abstract int getRecipeHeight();

    protected abstract void drawRecipe(PoseStack ms, R recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
}
