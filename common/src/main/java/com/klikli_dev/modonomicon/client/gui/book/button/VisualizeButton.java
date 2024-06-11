/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class VisualizeButton extends BookButton {

    public static final int U = 350;
    public static final int V = 12;
    public static final int HEIGHT = 7;
    public static final int WIDTH = 11;

    public VisualizeButton(BookEntryScreen parent, int x, int y, Button.OnPress onPress) {
        super(parent, x, y, U, V, WIDTH, HEIGHT,
                Component.translatable(Gui.BUTTON_VISUALIZE),
                onPress,
                Component.translatable(Gui.BUTTON_VISUALIZE_TOOLTIP)
        );
    }
}
