// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.node;

import com.klikli_dev.modonomicon.book.BookCategory;
import net.minecraft.client.gui.GuiGraphics;

public class DummyBookCategoryNodeScreen extends BookCategoryNodeScreen {
    public DummyBookCategoryNodeScreen(BookParentNodeScreen bookOverviewScreen, BookCategory category) {
        super(bookOverviewScreen, category);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //do not render entries
    }
}
