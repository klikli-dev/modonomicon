/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;

public class VisualizeButton extends BookButton {

    public static final int U = 350;
    public static final int V = 12;
    public static final int HEIGHT = 11;
    public static final int WIDTH = 11;

    public VisualizeButton(BookContentScreen parent, int x, int y, Button.OnPress onPress) {
        super(parent, x, y, U, V, WIDTH, HEIGHT,
                new TranslatableComponent(Gui.BUTTON_VISUALIZE),
                onPress,
                new TranslatableComponent(Gui.BUTTON_VISUALIZE_TOOLTIP)
        );
    }

    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(ms, mouseX, mouseY, partialTicks);
    }

}
