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
    private static MultiLayerScreen multiLayerScreen;

    @Override
    public void pushGuiLayer(Screen screen) {
        var minecraft = Minecraft.getInstance();
        var oldScreen = minecraft.gui.screen();

        if (multiLayerScreen == null) {
            multiLayerScreen = new MultiLayerScreen();
        }

        if (oldScreen != multiLayerScreen) {
            multiLayerScreen.guiLayers.clear();
            multiLayerScreen.guiLayers.push(oldScreen);
        }

        multiLayerScreen.guiLayers.push(screen);

        if (oldScreen != multiLayerScreen) {
            multiLayerScreen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
            minecraft.gui.setScreen(multiLayerScreen);
        }

        screen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.getNarrator().saySystemNow(screen.getNarrationMessage());
    }

    @Override
    public void popGuiLayer() {
        var minecraft = Minecraft.getInstance();
        if (multiLayerScreen == null || minecraft.gui.screen() != multiLayerScreen) {
            return;
        }

        if (multiLayerScreen.guiLayers.size() == 1) {
            minecraft.gui.setScreen(null);
        }

        var removed = multiLayerScreen.guiLayers.pop();
        removed.removed();

        if (!multiLayerScreen.guiLayers.isEmpty()) {
            minecraft.getNarrator().saySystemNow(multiLayerScreen.guiLayers.peek().getNarrationMessage());
        }
    }

    @Override
    public Screen getCurrentScreen() {
        if (multiLayerScreen == null) {
            return Minecraft.getInstance().gui.screen();
        }

        return multiLayerScreen.guiLayers.isEmpty() ? Minecraft.getInstance().gui.screen() : multiLayerScreen.guiLayers.peek();
    }
}
