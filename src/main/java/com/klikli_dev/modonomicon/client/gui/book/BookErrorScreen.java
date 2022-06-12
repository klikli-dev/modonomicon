/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class BookErrorScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;
    private static final ResourceLocation BOOK_CONTENT_TEXTURE = new ResourceLocation(Modonomicon.MODID, "textures/gui/book_content.png");
    private final Book book;
    private int bookLeft;
    private int bookTop;
    private Component errorText;

    public BookErrorScreen(Book book) {
        super(Component.literal(""));

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

    public void renderError(Component text, PoseStack pPoseStack, int x, int y, int width) {
        for (FormattedCharSequence formattedcharsequence : this.font.split(text, width)) {
            this.font.draw(pPoseStack, formattedcharsequence, x, y, 0);
            y += this.font.lineHeight;
        }
    }

    public void prepareError() {
        var errorHolder = BookErrorManager.get().getErrors(this.book.getId());
        if (errorHolder.getErrors().isEmpty()) {
            this.errorText = Component.translatable(Gui.NO_ERRORS_FOUND);
            Modonomicon.LOGGER.warn("No errors found for book {}, but error screen was opened!", this.book.getId());
        } else {
            var firstError = errorHolder.getErrors().get(0);

            var errorString = firstError.toString();
            if (errorHolder.getErrors().size() > 1) {
                errorString += "\n\n(" + (errorHolder.getErrors().size() - 1) + " more errors, see log)";
            }
            this.errorText = Component.literal(errorString);
        }
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
        this.renderError(this.errorText, pPoseStack, 15, 15, BOOK_BACKGROUND_WIDTH - 30);
        pPoseStack.popPose();
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
