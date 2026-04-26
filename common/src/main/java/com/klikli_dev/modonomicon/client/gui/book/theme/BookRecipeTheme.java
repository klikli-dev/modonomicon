/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookRecipeTheme {

    GuiSprite craftingGrid();

    default GuiSprite craftingArrow() {
        return GuiSprite.EMPTY;
    }

    GuiSprite shapelessIcon();

    GuiSprite processingRecipeBackground();

    GuiSprite smithingRecipeBackground();

    GuiSprite spotlightSlot();
}
