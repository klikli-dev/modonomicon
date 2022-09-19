/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.network.chat.TranslatableComponent;


public class ExitButton extends BookButton {

    public static final int U = 350;
    public static final int V = 0;
    public static final int HEIGHT = 12;
    public static final int WIDTH = 12;

    public ExitButton(BookContentScreen parent, int x, int y) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, () -> true,
                new TranslatableComponent(Gui.BUTTON_EXIT),
                parent::handleExitButton,
                new TranslatableComponent(Gui.BUTTON_EXIT) //button title equals hover text
        );
    }
}
