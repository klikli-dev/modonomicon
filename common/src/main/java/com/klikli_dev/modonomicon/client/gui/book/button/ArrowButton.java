/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ArrowButton extends BookButton {

    public static final int U = 300;
    public static final int V = 0;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 18;

    public final boolean left;

    public ArrowButton(BookScreenWithButtons parent, int x, int y, boolean left, Supplier<Boolean> displayCondition, OnPress onPress) {
        super(parent, x, y, U, left ? V + HEIGHT : V, WIDTH, HEIGHT, displayCondition,
                Component.translatable(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT),
                onPress,
                Component.translatable(left ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT)
                //button title equals hover text
        );
        this.left = left;
    }
}
