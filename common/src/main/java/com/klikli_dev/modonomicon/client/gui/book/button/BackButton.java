/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.network.chat.Component;

public class BackButton extends BookButton {

    public static final int U = 380;
    public static final int V = 0;
    public static final int HEIGHT = 9;
    public static final int WIDTH = 18;

    public BackButton(BookEntryScreen parent, int x, int y) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, parent::canSeeBackButton,
                Component.translatable(Gui.BUTTON_BACK),
                parent::handleBackButton,
                Component.translatable(Gui.BUTTON_BACK_TOOLTIP)
        );
    }
}
