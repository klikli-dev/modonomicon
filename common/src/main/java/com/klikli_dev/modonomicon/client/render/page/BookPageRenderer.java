/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.debug.BookDebugOverlay;
import com.klikli_dev.modonomicon.client.gui.TextWrapper;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookTextInteraction;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookLayoutTheme;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BookPageRenderer<T extends BookPage> {
    public static final class TextHolderBounds {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public TextHolderBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public int left;
    public int top;
    protected T page;
    protected BookEntryScreen parentScreen;
    protected Minecraft mc;
    protected Font font;

    private List<Button> buttons = new ArrayList<>();


    public BookPageRenderer(T page) {
        this.page = page;
    }


    public static float getBookTextHolderScaleForRenderSize(BookTextHolder text, Font font, int width, int height) {
        return BookTextInteraction.getBookTextHolderScaleForRenderSize(text, font, width, height);
    }

    /**
     * Renders pre-wrapped fragment lines at scale 1.0, using the exact coordinates given.
     * Used for the first fragment of split pages and for text-only continuation fragments.
     */
    public static void renderFragmentLines(GuiGraphicsExtractor guiGraphics, Font font, List<FormattedCharSequence> lines, int x, int y, int defaultTextColor) {
        float renderY = y;
        for (FormattedCharSequence line : lines) {
            TextRenderHelper.drawString(guiGraphics, font, line, x, renderY, defaultTextColor, false);
            renderY += font.lineHeight;
        }
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public static void renderBookTextHolder(GuiGraphicsExtractor guiGraphics, BookTextHolder text, Font font, int x, int y, int width, int height) {
        renderBookTextHolder(guiGraphics, text, font, x, y, width, height, 0);
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public static void renderBookTextHolder(GuiGraphicsExtractor guiGraphics, BookTextHolder text, Font font, int x, int y, int width, int height, int defaultTextColor) {
        if (text.hasComponent()) {
            //if it is a component, we draw it directly
            for (FormattedCharSequence formattedcharsequence : TextWrapper.split(text.getComponent(), width, font)) {
                guiGraphics.text(font, formattedcharsequence, x, y, defaultTextColor, false);
                y += font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var components = renderedText.getRenderedText();

            //DEBUG: draw the upper and lower boundary to see if our scaled text fits into it
//            guiGraphics.hLine(x, x + width, y + height, 0xFF0000FF);
//            guiGraphics.hLine(x, x + width, y, 0xFF0000FF);

            float scale = getBookTextHolderScaleForRenderSize(text, font, width, height);

            guiGraphics.pose().pushMatrix();
            if (scale < 1) {
                guiGraphics.pose().translate(x - x * scale, y - y * scale);
                guiGraphics.pose().scale(scale, scale);
            }

            float renderY = y;
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    TextRenderHelper.drawString(guiGraphics, font, formattedcharsequence, x, renderY, defaultTextColor, false);
                    renderY += font.lineHeight;
                }
            }
            guiGraphics.pose().popMatrix();
        } else {
            Modonomicon.LOG.warn("BookTextHolder with String {} has no component, but is not rendered to markdown either.", text.getString());
        }
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) index screen description text, applying the theme layout's index text offset. Will automatically handle markdown.
     */
    public static void renderIndexBookTextHolder(GuiGraphicsExtractor guiGraphics, BookTextHolder text, Font font, BookLayoutTheme layout, int x, int y, int width, int height, int defaultTextColor) {
        var bounds = applyTextOffset(layout.indexTextOffsetX(), layout.indexTextOffsetY(), layout.indexTextOffsetWidth(), layout.indexTextOffsetHeight(), x, y, width, height);
        renderBookTextHolder(guiGraphics, text, font, bounds.x, bounds.y, bounds.width, bounds.height, defaultTextColor);
    }

    /**
     * Call when the page is being set up to be displayed (when book content screen opens, or pages are changed)
     */
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        this.parentScreen = parentScreen;

        this.mc = Minecraft.getInstance();
        this.font = this.mc.font;
        this.left = left;
        this.top = top;

        this.buttons = new ArrayList<>();
    }

    public T getPage() {
        return this.page;
    }

    /**
     * Call when the page is will no longer be displayed (when book content screen opens, or pages are changed)
     */
    public void onEndDisplayPage(BookEntryScreen parentScreen) {
        parentScreen.removeRenderableWidgets(this.buttons);
    }

    /**
     * @param event localized to page x (mouseX - bookLeft - page.left) and y (mouseY - bookTop - page.top)
     */
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }

    /**
     * Called before the screen handles the click, e.g. before generic right-click navigation. This allows the page
     * to consume clicks on non-item ingredients (such as fluids in a recipe viewer preview), which the screen's
     * item stack handling does not cover.
     *
     * @param event localized to page x (mouseX - bookLeft - page.left) and y (mouseY - bookTop - page.top)
     * @return true if the click was handled
     */
    public boolean mouseClickedIngredient(MouseButtonEvent event) {
        return false;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     *
     * @deprecated use {@link #renderBookTextHolder(GuiGraphicsExtractor, BookTextHolder, Font, int, int, int, int)} instead and provide the desired height.
     * This exists only for backwards compatibility of custom pages and may estimate the wrong height.
     */
    @Deprecated
    public void renderBookTextHolder(GuiGraphicsExtractor guiGraphics, BookTextHolder text, int x, int y, int width) {
        var textY = 0;
        if (this instanceof PageWithTextRenderer pageWithTextRenderer)
            textY = pageWithTextRenderer.getTextY();

        renderBookTextHolder(guiGraphics, text, this.font, x, y, width, BookEntryScreen.PAGE_HEIGHT - textY, this.parentScreen.getBook().theme().palette().defaultTextColor());
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     * Applies the page's split/auto-scale configuration: split pages render only the first
     * fragment at scale 1.0, pages with auto-scaling disabled render unscaled without splitting.
     */
    public void renderBookTextHolder(GuiGraphicsExtractor guiGraphics, BookTextHolder text, int x, int y, int width, int height) {
        var bounds = this.getBookTextHolderBounds(x, y, width, height);
        var book = this.parentScreen != null ? this.parentScreen.getBook() : null;
        if (this.page != null && PageSplitter.isSplitEnabled(this.page) && book != null) {
            var continuationHeight = PageSplitter.continuationBounds(book).height;
            var fragments = PageSplitter.split(text, this.font, bounds.width, bounds.height, continuationHeight);
            var first = fragments.isEmpty() ? List.<FormattedCharSequence>of() : fragments.get(0);
            renderFragmentLines(guiGraphics, this.font, first, bounds.x, bounds.y, this.parentScreen.getBook().theme().palette().defaultTextColor());
            return;
        }
        if (this.page != null && !PageSplitter.isAutoScaleEnabled(this.page)) {
            var lines = PageSplitter.wrapForSplit(text, this.font, bounds.width);
            renderFragmentLines(guiGraphics, this.font, lines, bounds.x, bounds.y, this.parentScreen.getBook().theme().palette().defaultTextColor());
            return;
        }
        renderBookTextHolder(guiGraphics, text, this.font, bounds.x, bounds.y, bounds.width, bounds.height, this.parentScreen.getBook().theme().palette().defaultTextColor());
    }

    public TextHolderBounds getBookTextHolderBounds(int x, int y, int width, int height) {
        var layout = this.parentScreen.getBook().theme().layout();
        return applyTextOffset(layout.bookTextOffsetX(), layout.bookTextOffsetY(), layout.bookTextOffsetWidth(), layout.bookTextOffsetHeight(), x, y, width, height);
    }

    public static TextHolderBounds applyTextOffset(int offsetX, int offsetY, int offsetWidth, int offsetHeight, int x, int y, int width, int height) {
        x += offsetX;
        y += offsetY;

        height += offsetHeight;
        height -= offsetY; //always remove the offset y from the height to avoid overflow

        width += offsetWidth;
        width -= offsetX; //always remove the offset x from the width to avoid overflow

        return new TextHolderBounds(x, y, width, height);
    }

    /**
     * Will render the given BookTextHolder as (centered) title.
     */
    public void renderTitle(GuiGraphicsExtractor guiGraphics, BookTextHolder title, boolean showTitleSeparator, int x, int y) {

        guiGraphics.pose().pushMatrix();

        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //if user decided to use markdown title, we need to use the  rendered version
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());

            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(formattedCharSequence));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale);
                guiGraphics.pose().scale(scale, scale);
            }

            this.drawCenteredStringNoShadow(guiGraphics, formattedCharSequence, x, y, -1, scale);
        } else if (title.hasComponent()) {
            //non-markdown title we just render as usual

            var font = new FontDescription.Resource(BookDataManager.Client.get().safeFont(this.page.getBook().getFont()));

            var titleComponent = Component.empty().append(title.getComponent()).withStyle(s -> s.withFont(font));
            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(titleComponent.getVisualOrderText()));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale);
                guiGraphics.pose().scale(scale, scale);
            }

            //otherwise we use the component - that is either provided by the user, or created from the default title style.
            this.drawCenteredStringNoShadow(guiGraphics, titleComponent.getVisualOrderText(), x, y, -1, scale);
        } else {
            //this means a non-markdown title has no component -> this should not be possible, it indicates that either:
            // - a page did not set up its (non markdown) book text holder correctly in preprender markdown
            // - or a markdown title failed to render and remained a non-rendered book text holder
            BookErrorManager.get().setTo(this.page);
            BookErrorManager.get().error("Non-markdown title has no component.");
            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(null);
        }

        guiGraphics.pose().popMatrix();

        if (showTitleSeparator)
            BookContentRenderer.drawTitleSeparator(guiGraphics, this.page.getBook(), x, y + 12);
    }

    public abstract void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks);

    /**
     * Renders debug outlines for this page, if the debug overlay is enabled.
     * <p>
     * The default implementation visualizes the page bounds, the content area (everything above the text) and the
     * text area (including the configured text offsets). Override to add page specific regions.
     *
     * @param mouseX localized to page x (mouseX - bookLeft - page.left)
     * @param mouseY localized to page y (mouseY - bookTop - page.top)
     */
    public void renderDebugOverlay(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        //page bounds
        BookDebugOverlay.renderRegion(guiGraphics, 0, 0, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT,
                BookDebugOverlay.PAGE_FILL, BookDebugOverlay.PAGE_OUTLINE);

        if (this instanceof PageWithTextRenderer textRenderer) {
            int textY = textRenderer.getTextY();

            //content area: everything above the text
            BookDebugOverlay.renderRegion(guiGraphics, 0, 0, BookEntryScreen.PAGE_WIDTH, textY,
                    BookDebugOverlay.CONTENT_FILL, BookDebugOverlay.CONTENT_OUTLINE);

            //text area: the bounds the text holder is rendered into, after applying the theme text offsets
            var bounds = this.getBookTextHolderBounds(0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);
            BookDebugOverlay.renderRegion(guiGraphics, bounds.x, bounds.y, bounds.width, bounds.height,
                    BookDebugOverlay.TEXT_FILL, BookDebugOverlay.TEXT_OUTLINE);
        }
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, FormattedCharSequence s, int x, int y, int color, float scale) {
        TextRenderHelper.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, String s, int x, int y, int color, float scale) {
        TextRenderHelper.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawWrappedStringNoShadow(GuiGraphicsExtractor guiGraphics, Component s, int x, int y, int color, int width) {
        for (FormattedCharSequence formattedcharsequence : TextWrapper.split(s, width, this.font)) {
            guiGraphics.text(this.font, formattedcharsequence, x, y + (this.font.lineHeight), color, false);
            y += this.font.lineHeight;
        }
    }

    /**
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        return null;
    }

    protected void addButton(Button button) {
        button.setX(button.getX() + this.parentScreen.getBookLeft() + this.left);
        button.setY(button.getY() + this.parentScreen.getBookTop() + this.top);
        this.buttons.add(button);
        this.parentScreen.addRenderableWidget(button);
    }

    protected Style getClickedComponentStyleAtForTitle(BookTextHolder title, int x, int y, double pMouseX, double pMouseY) {
        return BookTextInteraction.hitTestTitle(this.font, this.page.getBook(), title, x, y, pMouseX, pMouseY);
    }

    /**
     * @deprecated use {@link #getClickedComponentStyleAtForTextHolder(BookTextHolder, int, int, int, int, double, double)} and provide the desired height.
     * This exists only for backwards compatibility of custom pages and may estimate the wrong height.
     */
    @Nullable
    @Deprecated
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, double pMouseX, double pMouseY) {
        var textY = 0;
        if (this instanceof PageWithTextRenderer pageWithTextRenderer)
            textY = pageWithTextRenderer.getTextY();
        return this.getClickedComponentStyleAtForTextHolder(text, x, y, width, BookEntryScreen.PAGE_HEIGHT - textY, pMouseX, pMouseY);
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, int height, double pMouseX, double pMouseY) {
        var book = this.parentScreen != null ? this.parentScreen.getBook() : null;
        return BookTextInteraction.hitTestTextHolder(this.font, this.page, book, text, x, y, width, height, pMouseX, pMouseY);
    }

    /**
     * Click/hover detection over pre-wrapped fragment lines at scale 1.0,
     * using the exact coordinates used for rendering.
     */
    @Nullable
    protected Style getClickedStyleAtFragmentLines(List<FormattedCharSequence> lines, float x, float y, double pMouseX, double pMouseY) {
        return BookTextInteraction.hitTestFragmentLines(this.font, lines, x, y, pMouseX, pMouseY);
    }
}
