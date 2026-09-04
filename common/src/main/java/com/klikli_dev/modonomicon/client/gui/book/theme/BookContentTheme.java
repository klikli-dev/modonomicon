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

    GuiButtonSprites visualizeLayersButton();

    GuiButtonSprites categoryScrollUpButton();

    GuiButtonSprites categoryScrollDownButton();

    GuiButtonSprites categoryButton();

    GuiSprite collectionButtonNormal();

    GuiSprite collectionButtonGolden();

    GuiSprite searchButtonIcon();

    GuiSprite showBookmarksButtonIcon();

    GuiSprite showRecentlyUnlockedButtonIcon();

    GuiSprite addBookmarkButtonIcon();

    GuiSprite removeBookmarkButtonIcon();

    GuiButtonSprites searchButton();

    GuiButtonSprites showBookmarksButton();

    GuiButtonSprites showRecentlyUnlockedButton();

    GuiButtonSprites addBookmarkButton();

    GuiButtonSprites removeBookmarkButton();

    GuiButtonSprites researchProgressButtonBackground();

    GuiButtonSprites researchAllButton();

    GuiButtonSprites researchNoneButton();

    GuiButtonSprites researchVisibleButton();

    GuiSprite searchFieldBackground();

    GuiSprite mediaFrame();

    GuiSprite craftingRecipeBackground();

    GuiSprite craftingGrid();

    GuiSprite craftingSlot();

    GuiSprite craftingArrow();

    GuiSprite shapelessIcon();

    GuiSprite processingRecipeBackground();

    GuiSprite smithingRecipeBackground();

    GuiSprite spotlightSlot();
}
