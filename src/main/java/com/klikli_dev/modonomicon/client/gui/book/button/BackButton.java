/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.network.chat.Component;

public class BackButton extends BookButton {

    public static final int U = 380;
    public static final int V = 0;
    public static final int HEIGHT = 9;
    public static final int WIDTH = 18;

    public BackButton(BookContentScreen parent, int x, int y) {
        super(parent, x, y, U, left ? V + HEIGHT : V, WIDTH, HEIGHT, () -> parent.canSeeArrowButton(left),
                Component.translatable(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT),
                parent::handleArrowButton,
                Component.translatable(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT)
                //button title equals hover text
        );
    }
}
