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

    GuiSprite requirementsSeparator();

    GuiSprite requirementCheck();

    /**
     * The complete button of a manually completed stage, in its normal, hovered and disabled states.
     */
    GuiSprite completeButtonNormal();

    GuiSprite completeButtonHover();

    GuiSprite completeButtonDisabled();

    /**
     * The faint line that connects the requirement icons of a category row, fading out to the right.
     */
    GuiSprite requirementRowLine();

    GuiSprite lockIcon();

    GuiButtonSprites unreadIndicator();

    GuiSprite markerIndicator();

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

    GuiButtonSprites entryTabRight();

    GuiButtonSprites entryTabLeft();

    GuiSprite entryTabRightActive();

    GuiSprite entryTabLeftActive();

    GuiSprite tabPageBackground();

    /**
     * The button that closes an open entry tab overlay and returns to the underlying pages.
     */
    GuiButtonSprites tabCloseButton();

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
