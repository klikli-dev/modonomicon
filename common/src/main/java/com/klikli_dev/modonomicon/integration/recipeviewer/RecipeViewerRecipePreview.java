// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/**
 * A recipe that a recipe viewer has prepared for rendering inside a Modonomicon page.
 * <p>
 * Instances are created by {@link RecipeViewerPlugin#createRecipePreview(RecipeViewerRecipe)} and are only
 * valid for as long as the viewer is available. They must not be referenced from code that runs without the
 * viewer mod being loaded.
 * <p>
 * All coordinates are relative to the page the preview is drawn on. The preview renders itself, including any
 * background or border, with its top-left corner at the given position and scaled by the given scale.
 */
public interface RecipeViewerRecipePreview {

    /**
     * @return the total unscaled width of the rendered preview, including any background or border
     */
    int getWidth();

    /**
     * @return the total unscaled height of the rendered preview, including any background or border
     */
    int getHeight();

    /**
     * Renders the preview at the given page-relative position and scale.
     *
     * @param guiGraphics the gui graphics to render into
     * @param x           the x position of the top-left corner, relative to the page
     * @param y           the y position of the top-left corner, relative to the page
     * @param mouseX      the mouse x position, relative to the page
     * @param mouseY      the mouse y position, relative to the page
     * @param scale       the scale to render at
     */
    void render(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float scale);

    /**
     * Advances the animation state of the preview. Should be called once per client tick.
     */
    void tick();

    /**
     * Returns the item stack currently displayed under the mouse, if any. Used to show its tooltip and to allow
     * the player to look up its recipes/usages.
     *
     * @param x      the x position of the top-left corner, relative to the page
     * @param y      the y position of the top-left corner, relative to the page
     * @param mouseX the mouse x position, relative to the page
     * @param mouseY the mouse y position, relative to the page
     * @param scale  the scale the preview is rendered at
     * @return the item stack under the mouse, or {@link ItemStack#EMPTY} if there is none
     */
    default ItemStack getItemStackUnderMouse(int x, int y, int mouseX, int mouseY, float scale) {
        return ItemStack.EMPTY;
    }

    /**
     * Tries to handle a click on an ingredient that is not an {@link ItemStack} (e.g. a fluid), by letting the
     * viewer look up the recipes/usages for the ingredient under the mouse.
     * <p>
     * This is only called when {@link #getItemStackUnderMouse} returned {@link ItemStack#EMPTY}, because item
     * stacks are handled by the book itself (tooltip + click-to-lookup).
     *
     * @param x      the x position of the top-left corner, relative to the page
     * @param y      the y position of the top-left corner, relative to the page
     * @param mouseX the mouse x position, relative to the page
     * @param mouseY the mouse y position, relative to the page
     * @param scale  the scale the preview is rendered at
     * @param uses   true to look up usages, false to look up recipes
     * @return true if the click was handled
     */
    default boolean handleIngredientClick(int x, int y, int mouseX, int mouseY, float scale, boolean uses) {
        return false;
    }

    /**
     * Renders the viewer's own overlays (e.g. non-item tooltips such as the shapeless crafting icon tooltip).
     * <p>
     * Called after the page has been rendered, for each preview the mouse is currently over. Unlike the other
     * methods this receives the real screen mouse position, because tooltips are positioned in screen space.
     *
     * @param x            the x position of the top-left corner, relative to the page
     * @param y            the y position of the top-left corner, relative to the page
     * @param mouseX       the mouse x position, relative to the page
     * @param mouseY       the mouse y position, relative to the page
     * @param scale        the scale the preview is rendered at
     * @param screenMouseX the mouse x position in screen coordinates
     * @param screenMouseY the mouse y position in screen coordinates
     */
    default void renderOverlays(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float scale, int screenMouseX, int screenMouseY) {
    }
}
