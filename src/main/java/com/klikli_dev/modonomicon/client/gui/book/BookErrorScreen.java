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

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class BookErrorScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;
    private static final ResourceLocation BOOK_CONTENT_TEXTURE = new ResourceLocation(Modonomicon.MODID, "textures/gui/book/book_content.png");
    private int bookLeft;
    private int bookTop;

    private final Book book;
    private Component errorText;

    public BookErrorScreen(Book book) {
        super(new TextComponent(""));

        this.book = book;

        this.minecraft = Minecraft.getInstance();
    }

    public void renderBookBackground(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BOOK_CONTENT_TEXTURE);

        int x = 0;
        int y = 0;

        GuiComponent.blit(poseStack, x, y, 0, 0, 272, 178, 512, 256);
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        this.renderBackground(pPoseStack);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderBookBackground(pPoseStack);
        pPoseStack.popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderError(this.errorText, pPoseStack, 10, 10, BOOK_BACKGROUND_WIDTH - 20);
        pPoseStack.popPose();
    }

    public void renderError(Component text, PoseStack pPoseStack, int x, int y, int width){
        for (FormattedCharSequence formattedcharsequence : this.font.split(text, width)) {
            this.font.draw(pPoseStack, formattedcharsequence, x, y, 0);
            y += this.font.lineHeight;
        }
    }

    public void prepareError(){
        var errorHolder = BookErrorManager.get().getErrors(book.getId());
        if(errorHolder.getErrors().isEmpty()){
            this.errorText = new TranslatableComponent(Gui.NO_ERRORS_FOUND);
            Modonomicon.LOGGER.warn("No errors found for book {}, but error screen was opened!", book.getId());
        } else {
            var firstError = errorHolder.getErrors().get(0);

            var errorString = firstError.toString();
            if(errorHolder.getErrors().size() > 1){
                errorString += "\n\n(" + (errorHolder.getErrors().size() - 1) + " more errors, see log)";
            }
            this.errorText = new TextComponent(errorString);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }


    @Override
    protected void init() {
        super.init();

        this.bookLeft = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        this.prepareError();

    }
}
