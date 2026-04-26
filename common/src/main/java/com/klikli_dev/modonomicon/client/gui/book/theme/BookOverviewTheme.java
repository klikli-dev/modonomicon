/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookOverviewTheme {

    GuiButtonSprites categoryButton();

    GuiButtonSprites searchButton();

    GuiButtonSprites showBookmarksButton();

    GuiButtonSprites showRecentlyUnlockedButton();

    GuiButtonSprites addBookmarkButton();

    GuiButtonSprites removeBookmarkButton();

    GuiButtonSprites readAllButton();

    GuiButtonSprites readNoneButton();

    GuiButtonSprites readUnlockedButton();
}
