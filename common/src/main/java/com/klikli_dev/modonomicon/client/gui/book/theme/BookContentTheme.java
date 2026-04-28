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

    GuiButtonSprites categoryButton();

    GuiButtonSprites searchButton();

    GuiButtonSprites showBookmarksButton();

    GuiButtonSprites showRecentlyUnlockedButton();

    GuiButtonSprites addBookmarkButton();

    GuiButtonSprites removeBookmarkButton();

    GuiButtonSprites readAllButton();

    GuiButtonSprites readNoneButton();

    GuiButtonSprites readUnlockedButton();

    GuiSprite searchFieldBackground();

    GuiSprite mediaFrame();

    GuiSprite craftingGrid();

    default GuiSprite craftingArrow() {
        return GuiSprite.EMPTY;
    }

    GuiSprite shapelessIcon();

    GuiSprite processingRecipeBackground();

    GuiSprite smithingRecipeBackground();

    GuiSprite spotlightSlot();
}
