/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ClientHooks;

public class NeoGuiHelper implements GuiHelper {
    @Override
    public void pushGuiLayer(Screen screen) {
        ClientHooks.pushGuiLayer(Minecraft.getInstance(), screen);
    }

    @Override
    public void popGuiLayer() {
        ClientHooks.popGuiLayer(Minecraft.getInstance());
    }

    @Override
    public Screen getCurrentScreen() {
        return Minecraft.getInstance().gui.screen();
    }
}
