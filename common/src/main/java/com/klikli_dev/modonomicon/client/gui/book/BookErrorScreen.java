/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class BookErrorScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;
    private final Book book;
    private int bookLeft;
    private int bookTop;
    private Component errorText;

    public BookErrorScreen(Book book) {
        super(Component.literal(""));

        this.book = book;
    }

    public void renderBookBackground(GuiGraphicsExtractor guiGraphics) {
        BookContentRenderer.renderBookBackground(guiGraphics, this.book);
    }

    public void renderError(GuiGraphicsExtractor guiGraphics, Component text, int x, int y, int width) {
        for (FormattedCharSequence formattedcharsequence : this.font.split(text, width)) {
            guiGraphics.text(this.font, formattedcharsequence, x, y, 1, false);
            y += this.font.lineHeight;
        }
    }

    public void prepareError() {
        var errorHolder = BookErrorManager.get().getErrors(this.book.getId());
        if (errorHolder.getErrors().isEmpty()) {
            this.errorText = Component.translatable(Gui.NO_ERRORS_FOUND);
            Modonomicon.LOG.warn("No errors found for book {}, but error screen was opened!", this.book.getId());
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);
        this.renderBookBackground(guiGraphics);
        guiGraphics.pose().popMatrix();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);
        this.renderError(guiGraphics, this.errorText, 15, 15, BOOK_BACKGROUND_WIDTH - 30);
        guiGraphics.pose().popMatrix();
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
