/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class FabricGuiHelper implements GuiHelper {

    private static FabricMultiLayerScreen multiLayerScreen;

    public static float getGuiFarPlane() {
        // 11000 units for the overlay background,
        // and 10000 units for each layered Screen,

        if(multiLayerScreen == null)
            return 11000.0F;

        return 11000.0F + 10000.0F * (1 + multiLayerScreen.guiLayers.size());
    }

    @Override
    public void pushGuiLayer(Screen screen) {
        var minecraft = Minecraft.getInstance();

        var oldScreen = minecraft.gui.screen();

        if(multiLayerScreen == null)
            multiLayerScreen = new FabricMultiLayerScreen();

        if (oldScreen != multiLayerScreen) {
            //if our layer screen is not the current screen then some other mod or vanilla/loader code has set a screen or null
            //we treat that as a clean slate.
            multiLayerScreen.guiLayers.clear();

            //then we put the previous screen as the first layer
            multiLayerScreen.guiLayers.push(oldScreen);
        }

        multiLayerScreen.guiLayers.push(screen);

        if (oldScreen != multiLayerScreen) {
            //init needs to happen after we added screens, because with an empty guiLayers stack we get errors
            multiLayerScreen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
            minecraft.gui.screen = multiLayerScreen;
        }

        screen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.getNarrator().saySystemNow(screen.getNarrationMessage());
    }

    @Override
    public void popGuiLayer() {
        var minecraft = Minecraft.getInstance();

        if(multiLayerScreen == null)
            return;

        if (minecraft.gui.screen() != multiLayerScreen) {
            //someone already overwrote screen, we exit
            return;
        }

        if (multiLayerScreen.guiLayers.size() == 1) {
            //we are at the last layer, so we close the screen
            //we do this here because then the last screen gets the related events from mc / modloader
            minecraft.setScreenAndShow(null);
        }

        var removed = multiLayerScreen.guiLayers.pop();
        removed.removed();

        if (!multiLayerScreen.guiLayers.isEmpty()) {
            minecraft.getNarrator().saySystemNow(multiLayerScreen.guiLayers.peek().getNarrationMessage());
        }
    }

    @Override
    public Screen getCurrentScreen() {
        if(multiLayerScreen == null)
            return Minecraft.getInstance().gui.screen();

        return multiLayerScreen.guiLayers.isEmpty() ? Minecraft.getInstance().gui.screen() : multiLayerScreen.guiLayers.peek();
    }

}
