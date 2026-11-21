/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookStageRequirementsPage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryRequirementsController;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.RequirementBlock;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a stage requirements page: optional title and text, then the requirement block of the stage at the bottom of the page.
 * Without an authored title, the title of the block (e.g. the stage title) is used.
 */
public class BookStageRequirementsPageRenderer extends BookPageRenderer<BookStageRequirementsPage> {

    private static final int TITLE_CENTER_X = BookEntryScreen.PAGE_WIDTH / 2;
    private static final int TITLE_Y = 0;
    private static final int TEXT_Y_WITH_TITLE = 17;
    private static final int TEXT_Y_WITHOUT_TITLE = -4;
    /**
     * The origin of the page content: the block, the text and the hover test are placed relative to it.
     */
    private static final int PAGE_ORIGIN = 0;

    /**
     * The requirement block drawn at the bottom of the page. Empty if there is nothing to show.
     */
    private RequirementBlock block = RequirementBlock.empty();
    @Nullable
    private BookTextHolder title;

    public BookStageRequirementsPageRenderer(BookStageRequirementsPage page) {
        super(page);
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        this.refreshBlock();
        this.setReservedBottomHeight(this.requirements().getBlockMaxHeight(this.page.getStage()));
    }

    private EntryRequirementsController requirements() {
        return this.parentScreen.getRequirements();
    }

    /**
     * The block is fetched every time, since browsing the stages of the research changes it.
     */
    private void refreshBlock() {
        this.block = this.requirements().getBlock(this.page.getStage());
        this.title = this.resolveTitle(this.block);
    }

    @Nullable
    private BookTextHolder resolveTitle(RequirementBlock block) {
        if (this.page.hasTitle()) {
            return this.page.getTitle();
        }
        if (block.title().getString().isEmpty()) {
            return null;
        }

        return new BookTextHolder(Component.empty().append(block.title()).withStyle(Style.EMPTY
                .withBold(true)
                .withColor(this.page.getBook().themeData().palette().defaultTitleColor())));
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        this.refreshBlock();
        if (this.title != null) {
            this.renderTitle(guiGraphics, this.title, true, TITLE_CENTER_X, TITLE_Y);
        }

        var textY = this.getTextY();
        if (this.page.hasText()) {
            this.renderBookTextHolder(guiGraphics, this.page.getText(), PAGE_ORIGIN, textY, BookEntryScreen.PAGE_WIDTH, this.getAvailablePageHeight() - textY);
        }

        if (!this.block.isEmpty()) {
            this.requirements().renderBlock(guiGraphics, this.block, PAGE_ORIGIN, this.requirements().getBlockTop(this.block, PAGE_ORIGIN),
                    BookEntryScreen.PAGE_WIDTH, mouseX, mouseY, true);
        }

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX + this.parentScreen.getBookLeft() + this.left, mouseY + this.parentScreen.getBookTop() + this.top);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        this.refreshBlock();
        if (this.block.isEmpty()) {
            return false;
        }

        return this.requirements().clickBlock(this.block, PAGE_ORIGIN, this.requirements().getBlockTop(this.block, PAGE_ORIGIN),
                BookEntryScreen.PAGE_WIDTH, event.x(), event.y());
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > PAGE_ORIGIN && pMouseY > PAGE_ORIGIN) {
            if (this.title != null) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.title, TITLE_CENTER_X, TITLE_Y, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            if (this.page.hasText()) {
                var textY = this.getTextY();
                var bounds = this.getBookTextHolderBounds(PAGE_ORIGIN, textY, BookEntryScreen.PAGE_WIDTH, this.getAvailablePageHeight() - textY);
                var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), bounds.x, bounds.y, bounds.width, bounds.height, pMouseX, pMouseY);
                if (textStyle != null) {
                    return textStyle;
                }
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    private int getTextY() {
        return this.title != null ? TEXT_Y_WITH_TITLE : TEXT_Y_WITHOUT_TITLE;
    }
}
