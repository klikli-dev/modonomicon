// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.function.Function;

public class BookContentRenderer {

    private static long lastTurnPageSoundTime;

    public static void drawFromContentTexture(RenderPipeline renderPipeline, GuiGraphics guiGraphics, Book book, int x, int y, int u, int v, int w, int h, int color) {
        guiGraphics.blit(renderPipeline, book.getBookContentTexture(), x, y, u, v, w, h, 512, 256, color);
    }

    public static void drawFromContentTexture(RenderPipeline renderPipeline, GuiGraphics guiGraphics, Book book, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(renderPipeline, book.getBookContentTexture(), x, y, u, v, w, h, 512, 256);
    }

    public static void drawTitleSeparator(GuiGraphics guiGraphics, Book book, int x, int y) {
        int w = 110;
        int h = 3;
        int rx = x - w / 2;

        var color = ARGB.colorFromFloat(0.8f, 1f, 1f,1f);
        //u and v are the pixel coordinates in our book_content_texture
        drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, book, rx, y, 0, 253, w, h, color);
    }

    public static void drawLock(GuiGraphics guiGraphics, Book book, int x, int y) {
        drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, book, x, y, 496, 0, 16, 16);
    }

    public static void drawLock(GuiGraphics guiGraphics, Book book, int x, int y, int color) {
        drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, book, x, y, 496, 0, 16, 16, color);
    }

    public static void playTurnPageSound(Book book) {
        if (ClientTicks.ticks - lastTurnPageSoundTime > 6) {
            var sound = BuiltInRegistries.SOUND_EVENT.getValue(book.getTurnPageSound());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, (float) (0.7 + Math.random() * 0.3)));
            lastTurnPageSoundTime = ClientTicks.ticks;
        }
    }

    public static void renderBookBackground(GuiGraphics guiGraphics, ResourceLocation bookContentTexture) {
        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, bookContentTexture, x, y, 0, 0, 272, 178, 512, 256);
    }


}
