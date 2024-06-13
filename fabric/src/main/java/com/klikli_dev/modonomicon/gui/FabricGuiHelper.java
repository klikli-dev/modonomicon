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

    private static final FabricMultiLayerScreen multiLayerScreen = new FabricMultiLayerScreen();

    public static float getGuiFarPlane() {
        // 11000 units for the overlay background,
        // and 10000 units for each layered Screen,

        return 11000.0F + 10000.0F * (1 + multiLayerScreen.guiLayers.size());
    }

    @Override
    public void pushGuiLayer(Screen screen) {
        var minecraft = Minecraft.getInstance();

        var oldScreen = minecraft.screen;

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
            multiLayerScreen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
            minecraft.screen = multiLayerScreen;
        }

        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.getNarrator().sayNow(screen.getNarrationMessage());
    }

    @Override
    public void popGuiLayer() {
        var minecraft = Minecraft.getInstance();

        if (minecraft.screen != multiLayerScreen) {
            //someone already overwrote screen, we exit
            return;
        }

        if(multiLayerScreen.guiLayers.size() == 1){
            //we are at the last layer, so we close the screen
            //we do this here because then the last screen gets the related events from mc / modloader
            minecraft.setScreen(null);
        }

        var removed = multiLayerScreen.guiLayers.pop();
        removed.removed();

        if (!multiLayerScreen.guiLayers.isEmpty()) {
            minecraft.getNarrator().sayNow(multiLayerScreen.guiLayers.peek().getNarrationMessage());
        }
    }

    @Override
    public Screen getCurrentScreen() {
        return multiLayerScreen.guiLayers.isEmpty() ? Minecraft.getInstance().screen : multiLayerScreen.guiLayers.peek();
    }

}
