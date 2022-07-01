/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ExitButton;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.SaveEntryStateMessage;
import com.klikli_dev.modonomicon.registry.SoundRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;

public class BookContentScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    public static final int TOP_PADDING = 18;
    public static final int LEFT_PAGE_X = 15;
    public static final int RIGHT_PAGE_X = 141;
    public static final int PAGE_WIDTH = 116;
    public static final int PAGE_HEIGHT = 128; //TODO: Adjust to what is real
    public static final int FULL_WIDTH = 272;
    public static final int FULL_HEIGHT = 180;

    private static long lastTurnPageSoundTime;
    private final BookOverviewScreen parentScreen;
    private final BookEntry entry;
    private final ResourceLocation bookContentTexture;
    public int ticksInBook;
    private BookPage leftPage;
    private BookPage rightPage;

    private BookPageRenderer<?> leftPageRenderer;
    private BookPageRenderer<?> rightPageRenderer;
    private int bookLeft;
    private int bookTop;
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<Component> tooltip;

    public BookContentScreen(BookOverviewScreen parentScreen, BookEntry entry) {
        super(Component.literal(""));

        this.minecraft = Minecraft.getInstance();

        this.parentScreen = parentScreen;
        this.entry = entry;

        this.bookContentTexture = this.parentScreen.getBook().getBookContentTexture();

        this.loadEntryState();
    }

    public static void drawFromTexture(PoseStack poseStack, Book book, int x, int y, int u, int v, int w, int h) {
        RenderSystem.setShaderTexture(0, book.getBookContentTexture());
        blit(poseStack, x, y, u, v, w, h, 512, 256);
    }

    public static void drawTitleSeparator(PoseStack poseStack, Book book, int x, int y) {
        int w = 110;
        int h = 3;
        int rx = x - w / 2;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F);
        //u and v are the pixel coordinates in our book_content_texture
        drawFromTexture(poseStack, book, rx, y, 0, 253, w, h);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void playTurnPageSound(Book book) {
        if (ClientTicks.ticks - lastTurnPageSoundTime > 6) {
            //TODO: make mod loader agnostic
            var sound = Registry.SOUND_EVENT.getOptional(book.getTurnPageSound()).orElse(SoundRegistry.TURN_PAGE.get());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, (float) (0.7 + Math.random() * 0.3)));
            lastTurnPageSoundTime = ClientTicks.ticks;
        }
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    public Book getBook() {
        return this.entry.getBook();
    }

    public void renderBookBackground(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.bookContentTexture);

        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        GuiComponent.blit(poseStack, x, y, 0, 0, 272, 178, 512, 256);
    }

    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    /**
     * Needs to use Button instead of ArrowButton to conform to Button.OnPress otherwise we can't use it as method
     * reference, which we need - lambda can't use this in super constructor call.
     */
    public void handleArrowButton(Button button) {
        this.changePage(((ArrowButton) button).left, true);
    }

    public void handleExitButton(Button button) {
        this.onClose();
    }

    @Override
    public void onClose() {
        Networking.sendToServer(new SaveEntryStateMessage(this.entry, this.openPagesIndex));
        this.parentScreen.getCurrentCategoryScreen().onCloseEntry(this);
        super.onClose();
    }

    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Will change to the specified page, if not open already
     */
    public void changePage(int pageIndex, boolean playSound) {
        int openPagesIndex = pageIndex / 2; //will floor, which is what we want
        if (openPagesIndex >= 0 && openPagesIndex < this.maxOpenPagesIndex) {
            if (this.openPagesIndex != openPagesIndex) {
                this.openPagesIndex = openPagesIndex;

                this.onPageChanged();
                if (playSound) {
                    playTurnPageSound(this.getBook());
                }
            }
        } else {
            Modonomicon.LOGGER.warn("Tried to change to page index {} corresponding with " +
                    "openPagesIndex {} but max open pages index is {}.", pageIndex, openPagesIndex, this.maxOpenPagesIndex);
        }
    }

    public Style getClickedComponentStyleAtForPage(BookPageRenderer<?> page, double pMouseX, double pMouseY) {
        if (page != null) {
            return page.getClickedComponentStyleAt(pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top);
        }

        return null;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        var leftPageClickedStyle = this.getClickedComponentStyleAtForPage(this.leftPageRenderer, pMouseX, pMouseY);
        if (leftPageClickedStyle != null) {
            return leftPageClickedStyle;
        }
        var rightPageClickedStyle = this.getClickedComponentStyleAtForPage(this.rightPageRenderer, pMouseX, pMouseY);
        return rightPageClickedStyle;
    }

    public int getBookLeft() {
        return this.bookLeft;
    }

    public int getBookTop() {
        return this.bookTop;
    }

    @SuppressWarnings("unchecked")
    public void removeRenderableWidgets(Collection<? extends Widget> widgets) {
        this.renderables.removeIf(widgets::contains);
        this.children().removeIf(c -> c instanceof Widget && widgets.contains(c));
        this.narratables.removeIf(n -> n instanceof Widget && widgets.contains(n));
    }

    protected void drawTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (this.tooltip != null) {
            this.renderComponentTooltip(pPoseStack, this.tooltip, pMouseX, pMouseY);
        }
    }

    protected boolean clickPage(BookPageRenderer<?> page, double mouseX, double mouseY, int mouseButton) {
        if (page != null) {
            return page.mouseClicked(mouseX - this.bookLeft - page.left, mouseY - this.bookTop - page.top, mouseButton);
        }

        return false;
    }

    protected void renderPage(PoseStack poseStack, BookPageRenderer<?> page, int pMouseX, int pMouseY, float pPartialTick) {
        if (page == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(page.left, page.top, 0);
        page.render(poseStack, pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top, pPartialTick);
        poseStack.popPose();
    }

    protected void beginDisplayPages() {
        //allow pages to clean up
        if (this.leftPageRenderer != null) {
            this.leftPageRenderer.onEndDisplayPage(this);
        }
        if (this.rightPageRenderer != null) {
            this.rightPageRenderer.onEndDisplayPage(this);
        }

        //get new pages
        var pages = this.entry.getPages();
        int leftPageIndex = this.openPagesIndex * 2;
        int rightPageIndex = leftPageIndex + 1;

        this.leftPage = leftPageIndex < pages.size() ? pages.get(leftPageIndex) : null;
        this.rightPage = rightPageIndex < pages.size() ? pages.get(rightPageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.leftPage != null) {
            this.leftPageRenderer = PageRendererRegistry.getPageRenderer(this.leftPage.getType()).create(this.leftPage);
            this.leftPageRenderer.onBeginDisplayPage(this, LEFT_PAGE_X, TOP_PADDING);
        } else {
            this.leftPageRenderer = null;
        }
        if (this.rightPage != null) {
            this.rightPageRenderer = PageRendererRegistry.getPageRenderer(this.rightPage.getType()).create(this.rightPage);
            this.rightPageRenderer.onBeginDisplayPage(this, RIGHT_PAGE_X, TOP_PADDING);
        } else {
            this.rightPageRenderer = null;
        }
    }

    protected void changePage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                playTurnPageSound(this.getBook());
            }
        }
    }

    protected void onPageChanged() {
        this.beginDisplayPages();
    }

    protected void resetTooltip() {
        this.tooltip = null;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.resetTooltip();

        this.renderBackground(pPoseStack);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderBookBackground(pPoseStack);
        pPoseStack.popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderPage(pPoseStack, this.leftPageRenderer, pMouseX, pMouseY, pPartialTick);
        this.renderPage(pPoseStack, this.rightPageRenderer, pMouseX, pMouseY, pPartialTick);
        pPoseStack.popPose();

        //do not translate tooltip, would mess up location
        this.drawTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    /**
     * Make public to access from pages
     */
    @Override
    public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    @Override // make public
    public void renderComponentHoverEffect(PoseStack pPoseStack, @Nullable Style style, int mouseX, int mouseY) {
        super.renderComponentHoverEffect(pPoseStack, style, mouseX, mouseY);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        if (pStyle != null) {
            var event = pStyle.getClickEvent();
            if (event != null) {
                if (event.getAction() == Action.CHANGE_PAGE) {

                    var link = BookLink.from(event.getValue());
                    var book = BookDataManager.get().getBook(link.bookId);
                    if (link.entryId != null) {
                        var entry = book.getEntry(link.entryId);
                        int page = link.pageNumber;
                        if (link.pageAnchor != null) {
                            page = entry.getPageNumberForAnchor(link.pageAnchor);
                        }

                        BookGuiManager.get().openEntry(link.bookId, link.entryId, page);
                    } else if (link.categoryId != null) {
                        BookGuiManager.get().openEntry(link.bookId, link.categoryId, null, 0);
                    } else {
                        BookGuiManager.get().openEntry(link.bookId, null, null, 0);
                    }
                    return true;
                }
            }
        }
        return super.handleComponentClicked(pStyle);
    }

    @Override
    protected void init() {
        super.init();

        this.bookLeft = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        this.maxOpenPagesIndex = (int) Math.ceil((float) this.entry.getPages().size() / 2);
        this.beginDisplayPages();

        this.addRenderableWidget(new ArrowButton(this, this.bookLeft - 4, this.bookTop + FULL_HEIGHT - 6, true));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft + FULL_WIDTH - 14, this.bookTop + FULL_HEIGHT - 6, false));
        this.addRenderableWidget(new ExitButton(this, this.bookLeft + FULL_WIDTH - 10, this.bookTop - 2));
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasShiftDown()) {
            this.ticksInBook++;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            var style = this.getClickedComponentStyleAt(pMouseX, pMouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        return this.clickPage(this.leftPageRenderer, pMouseX, pMouseY, pButton)
                || this.clickPage(this.rightPageRenderer, pMouseX, pMouseY, pButton)
                || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void loadEntryState() {
        var state = BookStateCapability.getEntryStateFor(this.parentScreen.getMinecraft().player, this.entry);
        if (state != null) {
            this.openPagesIndex = state.openPagesIndex;
        }
    }
}
