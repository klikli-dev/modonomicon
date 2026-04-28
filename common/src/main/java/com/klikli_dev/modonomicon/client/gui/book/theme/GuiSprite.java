/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import net.minecraft.resources.Identifier;

public record GuiSprite(Identifier texture, int width, int height) {

    public static final GuiSprite EMPTY = new GuiSprite(Identifier.fromNamespaceAndPath("minecraft", "missingno"), 0, 0);

    public boolean isEmpty() {
        return this.width <= 0 || this.height <= 0;
    }
}
