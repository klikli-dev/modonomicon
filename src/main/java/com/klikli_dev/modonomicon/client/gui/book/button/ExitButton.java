/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class ExitButton extends BookButton {

    public static final int U = 350;
    public static final int V = 0;
    public static final int HEIGHT = 12;
    public static final int WIDTH = 12;

    public ExitButton(BookContentScreen parent, int x, int y) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, () -> true,
                new TranslatableComponent("modonomicon.gui.button.exit"),
                parent::handleExitButton,
                new TranslatableComponent("modonomicon.gui.button.exit") //button title equals hover text
        );
    }
}
