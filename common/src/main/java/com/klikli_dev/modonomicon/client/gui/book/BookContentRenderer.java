// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiButtonSprites;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.function.Function;

public class BookContentRenderer {

    private static long lastTurnPageSoundTime;

    public static void drawFromContentTexture(RenderPipeline renderPipeline, GuiGraphicsExtractor guiGraphics, Book book, int x, int y, int u, int v, int w, int h, int color) {
        guiGraphics.blit(renderPipeline, book.getBookContentTexture(), x, y, u, v, w, h, 512, 256, color);
    }

    public static void drawFromContentTexture(RenderPipeline renderPipeline, GuiGraphicsExtractor guiGraphics, Book book, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(renderPipeline, book.getBookContentTexture(), x, y, u, v, w, h, 512, 256);
    }

    public static void drawTitleSeparator(GuiGraphicsExtractor guiGraphics, Book book, int x, int y) {
        var sprite = book.theme().content().titleSeparator();
        int w = sprite.width();
        int h = sprite.height();
        int rx = x - w / 2;

        var color = ARGB.colorFromFloat(0.8f, 1f, 1f,1f);
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, rx, y, color);
    }

    public static void drawLock(GuiGraphicsExtractor guiGraphics, Book book, int x, int y) {
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, book.theme().content().lockIcon(), x, y);
    }

    public static void drawLock(GuiGraphicsExtractor guiGraphics, Book book, int x, int y, int color) {
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, book.theme().content().lockIcon(), x, y, color);
    }

    public static void drawUnreadIndicator(GuiGraphicsExtractor guiGraphics, Book book, int x, int y, boolean hovered) {
        guiGraphics.pose().pushMatrix();
        drawButton(guiGraphics, book.theme().content().unreadIndicator(), x, y, hovered);
        guiGraphics.pose().popMatrix();
    }

    public static void drawSprite(GuiGraphicsExtractor guiGraphics, GuiSprite sprite, int x, int y) {
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, x, y);
    }

    public static void drawSprite(GuiGraphicsExtractor guiGraphics, GuiSprite sprite, int x, int y, int color) {
        GuiGraphicsExt.blitSprite(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, x, y, color);
    }

    public static void drawSpriteRegion(GuiGraphicsExtractor guiGraphics, GuiSprite sprite, int x, int y, int width, int height) {
        GuiGraphicsExt.blitSpriteRegion(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, x, y, 0, 0, width, height);
    }

    public static void drawSpriteRegion(GuiGraphicsExtractor guiGraphics, GuiSprite sprite, int x, int y, int width, int height, int color) {
        GuiGraphicsExt.blitSpriteRegion(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, x, y, 0, 0, width, height, color);
    }

    public static void drawButton(GuiGraphicsExtractor guiGraphics, GuiButtonSprites sprites, int x, int y, boolean hovered) {
        drawSprite(guiGraphics, sprites.state(hovered, false), x, y);
    }

    public static void playTurnPageSound(Book book) {
        if (ClientTicks.ticks - lastTurnPageSoundTime > 6) {
            var sound = BuiltInRegistries.SOUND_EVENT.getValue(book.getTurnPageSound());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, (float) (0.7 + Math.random() * 0.3)));
            lastTurnPageSoundTime = ClientTicks.ticks;
        }
    }

    public static void renderBookBackground(GuiGraphicsExtractor guiGraphics, Identifier bookContentTexture) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, bookContentTexture, 0, 0, 0, 0, 272, 178, 512, 256);
    }

    public static void renderBookBackground(GuiGraphicsExtractor guiGraphics, Book book) {
        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        drawSprite(guiGraphics, book.theme().content().doublePageBackground(), x, y);
    }


}
