// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer.jei;

import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRecipePreview;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/**
 * A {@link RecipeViewerRecipePreview} backed by a JEI {@link IRecipeLayoutDrawable}.
 * <p>
 * This mirrors how JEI renders recipe previews itself, see
 * {@code mezz.jei.gui.overlay.bookmarks.PreviewTooltipComponent}.
 */
public class JeiRecipeViewerRecipePreview implements RecipeViewerRecipePreview {

    private final IRecipeLayoutDrawable<?> drawable;
    private final IJeiRuntime runtime;

    public JeiRecipeViewerRecipePreview(IRecipeLayoutDrawable<?> drawable, IJeiRuntime runtime) {
        this.drawable = drawable;
        this.runtime = runtime;
    }

    @Override
    public int getWidth() {
        return this.drawable.getRectWithBorder().getWidth();
    }

    @Override
    public int getHeight() {
        return this.drawable.getRectWithBorder().getHeight();
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float scale) {
        var pose = guiGraphics.pose();
        pose.pushMatrix();
        try {
            pose.translate(x, y);
            if (scale != 1.0f) {
                pose.scale(scale, scale);
            }

            //the drawable lives in an unscaled local frame, so convert the mouse into that frame
            int localMouseX = toLocal(mouseX - x, scale);
            int localMouseY = toLocal(mouseY - y, scale);

            //position the layout so that its border starts at the local origin
            var border = this.getBorder();
            this.drawable.setPosition(border, border);
            this.drawable.drawRecipe(guiGraphics, localMouseX, localMouseY);
        } finally {
            pose.popMatrix();
        }
    }

    @Override
    public void tick() {
        this.drawable.tick();
    }

    @Override
    public ItemStack getItemStackUnderMouse(int x, int y, int mouseX, int mouseY, float scale) {
        this.drawable.setPosition(this.getBorder(), this.getBorder());
        int localMouseX = toLocal(mouseX - x, scale);
        int localMouseY = toLocal(mouseY - y, scale);
        return this.drawable.getItemStackUnderMouse(localMouseX, localMouseY).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean handleIngredientClick(int x, int y, int mouseX, int mouseY, float scale, boolean uses) {
        this.drawable.setPosition(this.getBorder(), this.getBorder());
        int localMouseX = toLocal(mouseX - x, scale);
        int localMouseY = toLocal(mouseY - y, scale);

        var slotUnderMouse = this.drawable.getSlotUnderMouse(localMouseX, localMouseY).orElse(null);
        if (slotUnderMouse == null) {
            return false;
        }

        var ingredient = slotUnderMouse.slot().getDisplayedIngredient().orElse(null);
        if (ingredient == null) {
            return false;
        }

        var role = uses ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
        var focus = this.runtime.getJeiHelpers().getFocusFactory().createFocus(role, ingredient);
        this.runtime.getRecipesGui().show(focus);
        return true;
    }

    @Override
    public void renderOverlays(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float scale, int screenMouseX, int screenMouseY) {
        int border = this.getBorder();
        int localMouseX = toLocal(mouseX - x, scale);
        int localMouseY = toLocal(mouseY - y, scale);

        //item stack tooltips are handled by the book itself, so skip those to avoid a duplicate tooltip
        this.drawable.setPosition(border, border);
        var slotUnderMouse = this.drawable.getSlotUnderMouse(localMouseX, localMouseY).orElse(null);
        if (slotUnderMouse != null) {
            var displayed = slotUnderMouse.slot().getDisplayedIngredient().orElse(null);
            if (displayed != null && displayed.getIngredient(VanillaTypes.ITEM_STACK).isPresent()) {
                return;
            }
        }

        //JEI computes the hovered slot from (mouse - area) but draws the tooltip at the given mouse position in
        //screen space. To keep both correct while the preview is scaled, we shift the layout so that the local
        //hit-test matches, while the tooltip is still placed at the real screen mouse.
        this.drawable.setPosition(screenMouseX - localMouseX + border, screenMouseY - localMouseY + border);
        this.drawable.drawOverlays(guiGraphics, screenMouseX, screenMouseY);
    }

    private static int toLocal(int pageRelative, float scale) {
        return Math.round(pageRelative / scale);
    }

    private int getBorder() {
        var withBorder = this.drawable.getRectWithBorder();
        var rect = this.drawable.getRect();
        return (withBorder.getWidth() - rect.getWidth()) / 2;
    }
}
