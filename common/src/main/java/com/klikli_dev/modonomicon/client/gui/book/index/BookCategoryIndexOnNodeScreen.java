/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.index;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * A special version of the BookCategoryIndexScreen that is intended to be rendered on top of a parent node screen (instead of a parent index screen)
 */
public class BookCategoryIndexOnNodeScreen extends BookCategoryIndexScreen {

    public BookCategoryIndexOnNodeScreen(BookParentScreen parentScreen, BookCategory category) {
        super(parentScreen, category, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);

        if (BookGuiManager.get().openBookParentScreen instanceof BookParentNodeScreen parentScreen) {
            parentScreen.renderMouseXOverride = pMouseX;
            parentScreen.renderMouseYOverride = pMouseY;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //we prevent the default background rendering because we still need to see the underlying parent node screen
    }

    @Override
    public void onClose() {
        //on close might come from paginated screen, and in our parent would ask the gui manager to close us
        //but this special index screen can only be closed together with the parent screen -> on esc.
        //so we simply don't do anything here.
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        //if we click outside, we don't close like the parent would, instead we let the click unhandled so the parent can handle it
        if (this.isClickOutsideEntry(event.x(), event.y())) {
            if (BookGuiManager.get().openBookParentScreen instanceof BookParentNodeScreen parentScreen) {
                return parentScreen.mouseClicked(event, isDoubleClick);
            }

            return false;
        }

        //with the "outside closing" prevented we can let our parents do the rest
        return super.mouseClicked(event, isDoubleClick);
    }
}
