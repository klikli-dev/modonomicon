/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class ArrowButton extends BookButton {

    public static final int U = 300;
    public static final int V = 0;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 18;

    public final boolean left;

    public ArrowButton(BookContentScreen parent, int x, int y, boolean left) {
        super(parent, x, y, U, left ? V + HEIGHT : V, WIDTH, HEIGHT, () -> parent.canSeeArrowButton(left),
                new TranslatableComponent(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT),
                parent::handleArrowButton,
                new TranslatableComponent(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT)
                //button title equals hover text
        );
        this.left = left;
    }
}
