/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class NeoGuiHelper implements GuiHelper {
    @Override
    public void pushGuiLayer(Screen screen) {
        Minecraft.getInstance().pushGuiLayer(screen);
    }

    @Override
    public void popGuiLayer() {
        Minecraft.getInstance().popGuiLayer();
    }

    @Override
    public Screen getCurrentScreen() {
        return Minecraft.getInstance().screen;
    }
}
