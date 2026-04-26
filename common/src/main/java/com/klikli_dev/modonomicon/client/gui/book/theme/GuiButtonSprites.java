/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import org.jetbrains.annotations.Nullable;

public record GuiButtonSprites(GuiSprite normal, GuiSprite hover, @Nullable GuiSprite pressed) {

    public GuiButtonSprites(GuiSprite normal, GuiSprite hover) {
        this(normal, hover, null);
    }

    public GuiSprite state(boolean hovered, boolean pressed) {
        if (pressed && this.pressed != null) {
            return this.pressed;
        }

        if (hovered && !this.hover.isEmpty()) {
            return this.hover;
        }

        return this.normal;
    }
}
