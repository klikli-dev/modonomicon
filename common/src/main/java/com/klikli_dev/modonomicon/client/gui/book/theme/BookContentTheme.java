/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookContentTheme {

    GuiSprite doublePageBackground();

    GuiSprite singlePageBackground();

    GuiSprite titleSeparator();

    GuiSprite lockIcon();

    GuiButtonSprites unreadIndicator();

    GuiButtonSprites nextPageButton();

    GuiButtonSprites previousPageButton();

    GuiButtonSprites smallNextPageButton();

    GuiButtonSprites smallPreviousPageButton();

    GuiButtonSprites backButton();

    GuiButtonSprites exitButton();

    GuiButtonSprites visualizeButton();

    GuiButtonSprites categoryScrollUpButton();

    GuiButtonSprites categoryScrollDownButton();

    GuiSprite searchFieldBackground();

    GuiSprite mediaFrame();
}
