/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayDeque;
import java.util.Deque;

public class ForgeGuiHelper implements GuiHelper {
    //Forge 65.x removed Minecraft.pushGuiLayer/popGuiLayer, so we maintain our own screen stack.
    private final Deque<Screen> screenStack = new ArrayDeque<>();

    @Override
    public void pushGuiLayer(Screen screen) {
        var mc = Minecraft.getInstance();
        if (mc.gui.screen() != null) {
            this.screenStack.push(mc.gui.screen());
        }
        mc.gui.setScreen(screen);
    }

    @Override
    public void popGuiLayer() {
        Minecraft.getInstance().gui.setScreen(this.screenStack.isEmpty() ? null : this.screenStack.pop());
    }

    @Override
    public Screen getCurrentScreen() {
        return Minecraft.getInstance().gui.screen();
    }
}
