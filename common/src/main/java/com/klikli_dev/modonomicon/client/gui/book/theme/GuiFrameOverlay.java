/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public record GuiFrameOverlay(GuiSprite sprite, int frameXOffset, int frameYOffset) {

    public int getFrameX(int startX) {
        return startX - this.sprite.width() / 2 + this.frameXOffset;
    }

    public int getFrameY(int startY) {
        return startY - this.sprite.height() / 2 + this.frameYOffset;
    }
}
