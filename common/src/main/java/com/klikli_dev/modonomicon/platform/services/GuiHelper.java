/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

import net.minecraft.client.gui.screens.Screen;

public interface GuiHelper {

    /**
     * Pushes a screen as a new GUI layer.
     *
     * @param screen the new GUI layer
     */
    void pushGuiLayer(Screen screen);

    /**
     * Pops a GUI layer from the screen.
     */
    void popGuiLayer();

    Screen getCurrentScreen();
}