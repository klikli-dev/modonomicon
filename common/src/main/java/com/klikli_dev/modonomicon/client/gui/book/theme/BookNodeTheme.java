/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookNodeTheme {

    GuiTexture entryBackground(String spriteId);

    GuiSprite smallCurveLeftDown();

    GuiSprite smallCurveRightDown();

    GuiSprite smallCurveLeftUp();

    GuiSprite smallCurveRightUp();

    GuiSprite largeCurveLeftDown();

    GuiSprite largeCurveRightDown();

    GuiSprite largeCurveLeftUp();

    GuiSprite largeCurveRightUp();

    GuiSprite verticalLine();

    GuiSprite horizontalLine();

    GuiSprite upArrow();

    GuiSprite downArrow();

    GuiSprite rightArrow();

    GuiSprite leftArrow();
}
