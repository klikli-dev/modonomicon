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
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BookPageRenderer<T extends BookPage> {
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
        if (!(text instanceof RenderedBookTextHolder renderedText))
            return 1.0f;

        var cachedScale = BookDataManager.Client.get().getScale(text, width, height);
        if(cachedScale > -1f)
            return cachedScale;

        var components = renderedText.getRenderedText();

        float granularity = 0.01F;
        float scale = 1.0F;
        float totalHeight = 0;
        do {
            //calculate total height by simulating rendering with the current scale.
            //this iterative approach is necessary because when scaling down we fit more words per line, resulting in less lines after wrapping.

            //first scale the width and calculate how many lines we have at this scale
            int totalLines = 0;
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), font);
                totalLines += wrapped.size();
            }

            //then calculate how high the amount of lines would be at this scale
            totalHeight = totalLines * font.lineHeight * scale;

            //now reduce scale for the next iteration
            //it is important to iterate with a fine granularity, otherwise the text will be downscaled way too much
            scale -= granularity;

            //repeat until we have a scale that fits the height
        } while(totalHeight > height);

        BookDataManager.Client.get().putScale(text, width, height, scale);

        return scale;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public static void renderBookTextHolder(GuiGraphics guiGraphics, BookTextHolder text, Font font, int x, int y, int width, int height) {
        if (text.hasComponent()) {
            //if it is a component, we draw it directly
            for (FormattedCharSequence formattedcharsequence : font.split(text.getComponent(), width)) {
                guiGraphics.drawString(font, formattedcharsequence, x, y, 0, false);
                y += font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var components = renderedText.getRenderedText();

            //DEBUG: draw the upper and lower boundary to see if our scaled text fits into it
//            guiGraphics.hLine(x, x + width, y + height, 0xFF0000FF);
//            guiGraphics.hLine(x, x + width, y, 0xFF0000FF);

            float scale = getBookTextHolderScaleForRenderSize(text, font, width, height);

            guiGraphics.pose().pushPose();

            if (scale < 1) {
                guiGraphics.pose().translate(x - x * scale, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }

            float renderY = y;
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    GuiGraphicsExt.drawString(guiGraphics, font, formattedcharsequence, x, renderY, 0, false);
                    renderY += font.lineHeight;
                }
            }

            guiGraphics.pose().popPose();

        } else {
            Modonomicon.LOG.warn("BookTextHolder with String {} has no component, but is not rendered to markdown either.", text.getString());
        }
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
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     * @deprecated use {@link #renderBookTextHolder(GuiGraphics, BookTextHolder, Font, int, int, int, int)} instead and provide the desired height.
     * This exists only for backwards compatibility of custom pages and may estimate the wrong height.
     */
    @Deprecated
    public void renderBookTextHolder(GuiGraphics guiGraphics, BookTextHolder text, int x, int y, int width) {
        var textY = 0;
        if(this instanceof PageWithTextRenderer pageWithTextRenderer)
            textY = pageWithTextRenderer.getTextY();

        renderBookTextHolder(guiGraphics, text, this.font, x, y, width, BookEntryScreen.PAGE_HEIGHT - textY);
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public void renderBookTextHolder(GuiGraphics guiGraphics, BookTextHolder text, int x, int y, int width, int height) {
        x += this.parentScreen.getBook().getBookTextOffsetX();
        y += this.parentScreen.getBook().getBookTextOffsetY();

        height += this.parentScreen.getBook().getBookTextOffsetHeight();
        height -= this.parentScreen.getBook().getBookTextOffsetY(); //always remove the offset y from the height to avoid overflow

        width += this.parentScreen.getBook().getBookTextOffsetWidth();
        width -= this.parentScreen.getBook().getBookTextOffsetX(); //always remove the offset x from the width to avoid overflow

        renderBookTextHolder(guiGraphics, text, this.font, x, y, width, height);
    }

    /**
     * Will render the given BookTextHolder as (centered) title.
     */
    public void renderTitle(GuiGraphics guiGraphics, BookTextHolder title, boolean showTitleSeparator, int x, int y) {

        guiGraphics.pose().pushPose();

        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //if user decided to use markdown title, we need to use the  rendered version
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());

            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(formattedCharSequence));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }

            this.drawCenteredStringNoShadow(guiGraphics, formattedCharSequence, x, y, 0, scale);
        } else if (title.hasComponent()) {
            //non-markdown title we just render as usual

            var font = BookDataManager.Client.get().safeFont(this.page.getBook().getFont());

            var titleComponent = Component.empty().append(title.getComponent()).withStyle(s -> s.withFont(font));
            //if title is larger than allowed, scaled to fit
            var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(titleComponent.getVisualOrderText()));
            if (scale < 1) {
                guiGraphics.pose().translate(0, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }

            //otherwise we use the component - that is either provided by the user, or created from the default title style.
            this.drawCenteredStringNoShadow(guiGraphics, titleComponent.getVisualOrderText(), x, y, 0, scale);
        } else {
            //this means a non-markdown title has no component -> this should not be possible, it indicates that either:
            // - a page did not set up its (non markdown) book text holder correctly in preprender markdown
            // - or a markdown title failed to render and remained a non-rendered book text holder
            BookErrorManager.get().setTo(this.page);
            BookErrorManager.get().error("Non-markdown title has no component.");
            BookErrorManager.get().getContextHelper().reset();
            BookErrorManager.get().setCurrentBookId(null);
        }

        guiGraphics.pose().popPose();

        if (showTitleSeparator)
            BookContentRenderer.drawTitleSeparator(guiGraphics, this.page.getBook(), x, y + 12);
    }

    public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks);

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, FormattedCharSequence s, int x, int y, int color, float scale) {
        GuiGraphicsExt.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, String s, int x, int y, int color, float scale) {
        GuiGraphicsExt.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public void drawWrappedStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color, int width) {
        for (FormattedCharSequence formattedcharsequence : this.font.split(s, width)) {
            guiGraphics.drawString(this.font, formattedcharsequence, x, y + (this.font.lineHeight), color, false);
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

    @Nullable
    protected Style getClickedComponentStyleAtForTitle(BookTextHolder title, int x, int y, double pMouseX, double pMouseY) {
        //they say good code comments itself. Well, this is not good code.
        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //markdown title
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        } else {
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                var formattedCharSequence = title.getComponent().getVisualOrderText();
                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        }
        return null;
    }

    /**
     * @deprecated use {@link #getClickedComponentStyleAtForTextHolder(BookTextHolder, int, int, int, int, double, double)} and provide the desired height.
     * This exists only for backwards compatibility of custom pages and may estimate the wrong height.
     */
    @Nullable
    @Deprecated
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, double pMouseX, double pMouseY) {
        var textY = 0;
        if(this instanceof PageWithTextRenderer pageWithTextRenderer)
            textY = pageWithTextRenderer.getTextY();
        return this.getClickedComponentStyleAtForTextHolder(text, x, y, width, BookEntryScreen.PAGE_HEIGHT - textY, pMouseX, pMouseY);
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, int height, double pMouseX, double pMouseY) {
        if (text.hasComponent()) {
            //we don't do math to get the current line, we just split and iterate.
            //why? Because performance should not matter (significantly enough to bother)
            for (FormattedCharSequence formattedcharsequence : this.font.split(text.getComponent(), width)) {
                if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                    //check if we are vertically over the title line
                    //horizontally over and right of the title is handled by font splitter
                    return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) pMouseX - x);
                }
                y += this.font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var scale = getBookTextHolderScaleForRenderSize(text, this.font, width, height);

            var components = renderedText.getRenderedText();
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), this.font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    float minY =  y * scale;
                    float maxY = (y + this.font.lineHeight) * scale;
                    if (pMouseY > minY && pMouseY < maxY) {
                        //check if we are vertically over the title line
                        //horizontally over and right of the title is handled by font splitter
                        return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) ((pMouseX - x) / scale));
                    }
                    y += this.font.lineHeight;
                }
            }
        }

        return null;
    }
}
