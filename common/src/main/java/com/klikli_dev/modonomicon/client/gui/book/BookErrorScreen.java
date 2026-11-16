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
import com.klikli_dev.modonomicon.client.gui.TextWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookErrorScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;
    private final Identifier bookId;
    @Nullable
    private final Book book;
    private int bookLeft;
    private int bookTop;
    private Component errorText;

    public BookErrorScreen(Identifier bookId, @Nullable Book book) {
        super(Component.literal(""));

        this.bookId = bookId;
        this.book = book;
    }

    public void renderBookBackground(GuiGraphicsExtractor guiGraphics) {
        if (this.book == null) {
            return;
        }
        BookContentRenderer.renderBookBackground(guiGraphics, this.book);
    }

    public void renderError(GuiGraphicsExtractor guiGraphics, Component text, int x, int y, int width) {
        for (FormattedCharSequence formattedcharsequence : TextWrapper.split(text, width, this.font)) {
            guiGraphics.text(this.font, formattedcharsequence, x, y, 0xFF000000, false);
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
            var firstError = errorHolder.getFirstBlockingError();
            if (firstError == null) {
                firstError = errors.get(0);
            }

            var errorString = firstError.toString();
            if (errors.size() > 1) {
                errorString += "\n\n(" + (errors.size() - 1) + " more errors, see log)";
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
    public boolean keyPressed(KeyEvent event) {
        if (this.minecraft.options.keyInventory.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
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
