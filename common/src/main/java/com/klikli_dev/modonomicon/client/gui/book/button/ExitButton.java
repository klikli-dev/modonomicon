/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import net.minecraft.network.chat.Component;


public class ExitButton extends BookButton {

    public static final int U = 350;
    public static final int V = 0;
    public static final int HEIGHT = 12;
    public static final int WIDTH = 12;

    public ExitButton(BookScreenWithButtons parent, int x, int y, OnPress onPress) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, () -> true,
                Component.translatable(Gui.BUTTON_EXIT),
                onPress,
                Component.translatable(Gui.BUTTON_EXIT) //button title equals hover text
        );
    }
}
