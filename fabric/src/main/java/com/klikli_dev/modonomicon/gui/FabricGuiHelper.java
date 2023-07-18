package com.klikli_dev.modonomicon.gui;

import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class FabricGuiHelper implements GuiHelper {
    @Override
    public void pushGuiLayer(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    public void popGuiLayer() {
        Minecraft.getInstance().setScreen(null);
    }
}
