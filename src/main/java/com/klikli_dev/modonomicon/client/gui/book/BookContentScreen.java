/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.data.book.BookChapter;
import com.klikli_dev.modonomicon.data.book.page.AbstractBookPage;
import com.klikli_dev.modonomicon.data.book.page.BookTextPage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class BookContentScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    private final BookOverviewScreen parentScreen;
    private final BookChapter chapter;

    private final ResourceLocation bookContentTexture;

    public BookContentScreen(BookOverviewScreen parentScreen, BookChapter chapter) {
        super(new TextComponent(""));

        this.minecraft = Minecraft.getInstance();

        this.parentScreen = parentScreen;
        this.chapter = chapter;

        this.bookContentTexture = this.parentScreen.getBook().getBookContentTexture();
    }

    public BookChapter getChapter() {
        return this.chapter;
    }

    public void renderBookBackground(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.bookContentTexture);

        int x = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        GuiComponent.blit(poseStack, x, y, 0, 0, 272, 178, 512, 256);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);

        this.renderBookBackground(pPoseStack);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        //TODO: use page interface here
        //TODO: properly place left/right page
        for(var page : this.chapter.getPages()){
            if(page instanceof AbstractBookPage bookPage){
                bookPage.init(this.chapter, this, 0, 20, 20);
                bookPage.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
