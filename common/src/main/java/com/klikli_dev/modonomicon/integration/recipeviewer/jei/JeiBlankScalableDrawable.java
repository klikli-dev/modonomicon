// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer.jei;

import mezz.jei.api.gui.drawable.IScalableDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A no-op {@link IScalableDrawable}, used as the background of a recipe layout to suppress drawing the recipe's
 * own background (and border).
 */
public class JeiBlankScalableDrawable implements IScalableDrawable {

    public static final JeiBlankScalableDrawable INSTANCE = new JeiBlankScalableDrawable();

    private JeiBlankScalableDrawable() {
    }

    @Override
    public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        //no-op
    }
}
