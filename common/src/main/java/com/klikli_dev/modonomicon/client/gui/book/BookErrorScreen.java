/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.error.BookErrorInfo;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookErrorScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;
    private static final ResourceLocation BOOK_CONTENT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "textures/gui/book_content.png");
    private final ResourceLocation bookId;
    @Nullable
    private final Book book;
    private int bookLeft;
    private int bookTop;
    private Component errorText;

    public BookErrorScreen(ResourceLocation bookId, @Nullable Book book) {
        super(Component.literal(""));

        this.bookId = bookId;
        this.book = book;

        this.minecraft = Minecraft.getInstance();
    }

    public void renderBookBackground(GuiGraphics guiGraphics) {
        int x = 0;
        int y = 0;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BOOK_CONTENT_TEXTURE, x, y, 0, 0, 272, 178, 512, 256);
    }

    public void renderError(GuiGraphics guiGraphics, Component text, int x, int y, int width) {
        for (FormattedCharSequence formattedcharsequence : this.font.split(text, width)) {
            guiGraphics.drawString(this.font, formattedcharsequence, x, y, 0, false);
            y += this.font.lineHeight;
        }
    }

    public void prepareError() {
        if (this.book == null) {
            this.errorText = Component.translatable(Gui.BOOK_NOT_FOUND, this.bookId.toString());
            return;
        }

        var errorHolder = BookErrorManager.get().getErrors(this.book.getId());
        var errors = errorHolder == null ? List.<BookErrorInfo>of() : errorHolder.getErrors();

        var buildError = this.book.getBuildError();
        if (buildError != null) {
            var errorString = Component.translatable(Gui.BOOK_BUILD_FAILED).getString() + "\n\n" + buildError;
            //the build error is usually also part of the recorded errors, do not count it twice
            var otherErrors = errors.stream().filter(e -> e != buildError).count();
            if (otherErrors > 0) {
                errorString += "\n\n(" + otherErrors + " more errors, see log)";
            }
            this.errorText = Component.literal(errorString);
        } else if (errors.isEmpty()) {
            this.errorText = Component.translatable(Gui.NO_ERRORS_FOUND);
            Modonomicon.LOG.warn("No errors found for book {}, but error screen was opened!", this.book.getId());
        } else {
            var firstError = errors.get(0);

            var errorString = firstError.toString();
            if (errors.size() > 1) {
                errorString += "\n\n(" + (errors.size() - 1) + " more errors, see log)";
            }
            this.errorText = Component.literal(errorString);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        this.renderBookBackground(guiGraphics);
        guiGraphics.pose().popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        this.renderError(guiGraphics, this.errorText, 15, 15, BOOK_BACKGROUND_WIDTH - 30);
        guiGraphics.pose().popPose();
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
