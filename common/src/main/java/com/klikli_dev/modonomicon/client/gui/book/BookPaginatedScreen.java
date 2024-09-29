/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ExitButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public abstract class BookPaginatedScreen extends Screen implements BookScreenWithButtons {

    public static final int FULL_WIDTH = 272;
    public static final int FULL_HEIGHT = 180;

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    protected int bookLeft;
    protected int bookTop;

    protected boolean addExitButton;

    public BookPaginatedScreen(Component component) {
        this(component, true);
    }

    public BookPaginatedScreen(Component component, boolean addExitButton) {
        super(component);

        this.addExitButton = addExitButton;
    }

    @Override
    protected void init() {
        this.bookLeft = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        this.initNavigationButtons();
    }

    protected void initNavigationButtons() {
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft - 4, this.bookTop + FULL_HEIGHT - 6, true, () -> this.canSeeArrowButton(true), this::handleArrowButton));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft + FULL_WIDTH - 14, this.bookTop + FULL_HEIGHT - 6, false, () -> this.canSeeArrowButton(false), this::handleArrowButton));
        if (this.addExitButton) {
            this.addRenderableWidget(new ExitButton(this, this.bookLeft + FULL_WIDTH - 10, this.bookTop - 2, this::handleExitButton));
        }
    }

    public void handleExitButton(Button button) {
        this.onClose();
    }

    public abstract boolean canSeeArrowButton(boolean left);

    /**
     * Needs to use Button instead of ArrowButton to conform to Button.OnPress otherwise we can't use it as method
     * reference, which we need - lambda can't use this in super constructor call.
     */
    public void handleArrowButton(Button button) {
        this.flipPage(((ArrowButton) button).left, true);
    }

    protected abstract void flipPage(boolean left, boolean playSound);

    protected boolean isClickOutsideEntry(double pMouseX, double pMouseY) {
        return pMouseX < this.bookLeft - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseX > this.bookLeft + BookEntryScreen.FULL_WIDTH + BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseY < this.bookTop - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseY > this.bookTop + BookEntryScreen.FULL_HEIGHT + BookEntryScreen.CLICK_SAFETY_MARGIN;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.back();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean canSeeBackButton() {
        return BookGuiManager.get().getHistorySize(this.getBook().getId()) > 0;
    }

    public void handleBackButton(Button button) {
        this.back();
    }

    public void back() {
        if (BookGuiManager.get().getHistorySize(this.getBook().getId()) > 0) {
            var lastPage = BookGuiManager.get().popHistory(this.getBook().getId());
            BookGuiManager.get().openEntry(lastPage.bookId(), lastPage.categoryId(), lastPage.entryId(), lastPage.page());
        } else {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isClickOutsideEntry(pMouseX, pMouseY)) {
            this.onClose();
            return true; //need to return, otherwise a right click outside the entry causes a double-close (the whole book, due to calling .back() below)
        }

        if (pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.back();
            return true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0) {
            this.flipPage(false, true);
        } else if (scrollY > 0) {
            this.flipPage(true, true);
        }

        return true;
    }
}
