/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Stack;

public class FabricGuiHelper implements GuiHelper {

    private static final Stack<Screen> guiLayers = new Stack<>();

    @Override
    public void pushGuiLayer(Screen screen) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.screen != null)
            guiLayers.push(minecraft.screen);
        minecraft.setScreen(screen);
    }

    @Override
    public void popGuiLayer() {
        var minecraft = Minecraft.getInstance();
        if (guiLayers.size() == 0) {
            minecraft.setScreen(null);
            return;
        }
        minecraft.setScreen(guiLayers.pop());
    }
}
