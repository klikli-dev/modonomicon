/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class CategoryScrollButton extends BookButton {

    public static final int U = 398;
    public static final int V = 12;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 14;

    public final boolean bottom;

    public CategoryScrollButton(BookParentNodeScreen parent, int x, int y, boolean bottom, Supplier<Boolean> displayCondition, OnPress onPress) {
        super(parent, x, y, U, bottom ? V : V + HEIGHT, WIDTH, HEIGHT, displayCondition,
                Component.translatable(bottom ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT),
                onPress,
                Component.translatable(bottom ? Gui.BUTTON_PREVIOUS : Gui.BUTTON_NEXT)
                //button title equals hover text
        );
        this.bottom = bottom;
    }
}
