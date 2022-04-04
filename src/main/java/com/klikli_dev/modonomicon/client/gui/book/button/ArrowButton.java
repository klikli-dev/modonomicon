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

public class ArrowButton extends ConditionalButton {

    public static final int U = 300;
    public static final int V = 0;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 18;

    public final boolean left;

    public ArrowButton(BookContentScreen parent, int x, int y, boolean left) {
        super(parent, x, y, U, left ? V + HEIGHT : V, WIDTH, HEIGHT, () -> parent.canSeeArrowButton(left),
                new TranslatableComponent(left ? "modonomicon.gui.button.previous_page" : "modonomicon.gui.button.next_page"),
                parent::handleArrowButton
        );
        this.left = left;
    }
}
