/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class VisualizeLayersButton extends BookButton {

    public static final int U = 350;
    public static final int V = 12;
    public static final int HEIGHT = 7;
    public static final int WIDTH = 11;

    public VisualizeLayersButton(BookEntryScreen parent, int x, int y, Button.OnPress onPress) {
        super(parent, x, y, WIDTH, HEIGHT, theme -> theme.content().visualizeLayersButton(),
                Component.translatable(Gui.BUTTON_VISUALIZE_LAYERS),
                onPress,
                Component.translatable(Gui.BUTTON_VISUALIZE_LAYERS_TOOLTIP)
        );
    }
}
