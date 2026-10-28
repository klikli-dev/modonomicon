// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.node;

import com.klikli_dev.modonomicon.book.BookCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class DummyBookCategoryNodeScreen extends BookCategoryNodeScreen {
    public DummyBookCategoryNodeScreen(BookParentNodeScreen bookOverviewScreen, BookCategory category) {
        super(bookOverviewScreen, category);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //do not render entries
    }

    @Override
    public void renderEntryTooltips(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //do not render tooltips for entries that are not rendered
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        //do not allow clicking entries that are not rendered
        return false;
    }
}
