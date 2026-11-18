/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Closes the open entry tab overlay and returns to the underlying pages.
 * Only shown (and clickable) while a tab is open.
 */
public class TabCloseButton extends BookButton {

    public static final int WIDTH = 12;
    public static final int HEIGHT = 12;

    public TabCloseButton(BookScreenWithButtons parent, int x, int y, Supplier<Boolean> displayCondition, OnPress onPress) {
        super(parent, x, y, WIDTH, HEIGHT, theme -> theme.content().tabCloseButton(), displayCondition,
                Component.translatable(Gui.BUTTON_CLOSE_TAB),
                onPress,
                Component.translatable(Gui.BUTTON_CLOSE_TAB) //button title equals hover text
        );
    }
}
