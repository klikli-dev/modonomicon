/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import com.klikli_dev.modonomicon.client.debug.BookDebugOverlay;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookFeedback;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.AddBookmarkButton;
import com.klikli_dev.modonomicon.client.gui.book.button.BackButton;
import com.klikli_dev.modonomicon.client.gui.book.button.BookSideButtonRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.RemoveBookmarkButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler.*;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.client.render.page.PageSplitter;
import com.klikli_dev.modonomicon.client.render.page.PageWithTextRenderer;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.fluid.FluidHolder;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry;
import com.klikli_dev.modonomicon.networking.AddBookmarkMessage;
import com.klikli_dev.modonomicon.networking.SyncBookVisualStatesMessage;
import com.klikli_dev.modonomicon.networking.SyncResearchStateMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.platform.services.FluidHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BookEntryScreen extends BookPaginatedScreen implements ContentRenderingScreen {

    /**
     * A transient client-only display entry. Authored pages stay canonical in
     * {@code BookContentEntry.pages}; split-enabled pages expand into one display entry
     * per fragment. Continuation fragments (index &gt; 0) carry pre-wrapped text-only lines
     * and have no public IDs or page numbers.
     */
    public record DisplayPage(BookPage page, int fragmentIndex, List<FormattedCharSequence> fragmentLines) {
        public boolean isContinuation() {
            return this.fragmentIndex > 0;
        }
    }

    public static final int TOP_PADDING = 15;
    public static final int LEFT_PAGE_X = 12;
    public static final int RIGHT_PAGE_X = 141;
    public static final int SINGLE_PAGE_X = LEFT_PAGE_X;
    public static final int PAGE_WIDTH = 124;
    public static final int PAGE_HEIGHT = 155;

    public static final int MAX_TITLE_WIDTH = PAGE_WIDTH - 4;

    public static final int CLICK_SAFETY_MARGIN = 20;

    protected final BookParentScreen parentScreen;
    protected final BookContentEntry entry;

    protected int ticksInBook;
    protected List<BookPage> unlockedPages;

    /**
     * Transient client-only expansion of {@link #unlockedPages}: split-enabled authored pages
     * appear once per fragment. Visual paging operates on this list.
     */
    protected List<DisplayPage> displayPages = List.of();

    private String lastSplitLocale;
    private Font lastSplitFont;
    private long lastSplitReloadGeneration = -1;

    /**
     * The index of the leftmost unlocked page being displayed.
     */
    protected int openPagesIndex;
    protected List<LinkHandler> linkHandlers;
    private List<Component> tooltip;
    private ItemStack tooltipStack;
    private FluidHolder tooltipFluidStack;
    private boolean isHoveringItemLink;

    public BookEntryScreen(BookParentScreen parentScreen, BookContentEntry entry) {
        super(Component.literal(""));

        this.parentScreen = parentScreen;

        this.entry = entry;
        //We're doing that here to ensure unlockedPages is available for state modification during loading
        this.unlockedPages = this.entry.getUnlockedPagesFor(this.minecraft.player);

        this.linkHandlers = List.of(
                new BookLinkHandler(this),
                new PatchouliLinkHandler(this),
                new ItemLinkHandler(this),
                new CommandLinkHandler(this)
        );
    }

    @Override
    public Book getBook() {
        return this.entry.getBook();
    }

    @Override
    public BookContentEntry getEntry() {
        return this.entry;
    }

    @Override
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public int getTicksInBook() {
        return this.ticksInBook;
    }

    @Override
    public void setTooltip(List<Component> tooltip) {
        this.resetTooltip();
        this.tooltip = tooltip;
    }

    @Override
    public void setTooltipStack(ItemStack stack) {
        this.resetTooltip();
        this.tooltipStack = stack;
    }

    @Override
    public void setTooltipStack(FluidHolder stack) {
        this.resetTooltip();
        this.tooltipFluidStack = stack;
    }

    @Override
    public int getBookLeft() {
        return this.bookLeft;
    }

    @Override
    public int getBookTop() {
        return this.bookTop;
    }

    @Override
    public boolean isHoveringItemLink(){
        return this.isHoveringItemLink;
    }

    @Override
    public void isHoveringItemLink(boolean value){
        this.isHoveringItemLink = value;
    }

    public int getCurrentPageNumber() {
        if (!this.displayPages.isEmpty() && this.openPagesIndex >= 0 && this.openPagesIndex < this.displayPages.size()) {
            return this.displayPages.get(this.openPagesIndex).page().getPageNumber();
        }
        return this.unlockedPages.get(this.openPagesIndex).getPageNumber();
    }

    /**
     * Builds the transient display list from the visible authored pages, expanding
     * split-enabled pages into one entry per fragment. A still-valid current fragment
     * is kept (e.g. init after back-history restored a continuation); only a stale
     * fragment is re-resolved to the first fragment of its authored page.
     */
    protected void rebuildDisplayPages() {
        int currentDisplay = -1;
        int currentAuthoredPage = -1;
        if (!this.displayPages.isEmpty() && this.openPagesIndex >= 0 && this.openPagesIndex < this.displayPages.size()) {
            currentDisplay = this.openPagesIndex;
            currentAuthoredPage = this.displayPages.get(currentDisplay).page().getPageNumber();
        }

        var font = Minecraft.getInstance() != null ? Minecraft.getInstance().font : null;
        var book = this.entry != null ? this.entry.getBook() : null;

        List<DisplayPage> pages = new ArrayList<>();
        for (var page : this.unlockedPages) {
            var textY = font != null && book != null ? this.getSplitTextY(page) : Integer.MIN_VALUE;
            if (textY != Integer.MIN_VALUE) {
                var fragments = PageSplitter.splitForPage(page, book, font, textY);
                if (fragments.size() <= 1) {
                    pages.add(new DisplayPage(page, 0, null));
                } else {
                    pages.add(new DisplayPage(page, 0, null));
                    for (int i = 1; i < fragments.size(); i++) {
                        pages.add(new DisplayPage(page, i, fragments.get(i)));
                    }
                }
            } else {
                pages.add(new DisplayPage(page, 0, null));
            }
        }
        this.displayPages = List.copyOf(pages);

        if (font != null) {
            this.lastSplitFont = font;
        }
        if (Minecraft.getInstance() != null && Minecraft.getInstance().getLanguageManager() != null) {
            this.lastSplitLocale = Minecraft.getInstance().getLanguageManager().getSelected();
        }
        this.lastSplitReloadGeneration = BookDataManager.Client.get().reloadGeneration();

        if (currentDisplay >= 0 && currentDisplay < this.displayPages.size()
                && this.displayPages.get(currentDisplay).page().getPageNumber() == currentAuthoredPage) {
            // Split is unchanged for the current fragment: keep the exact virtual page.
            this.openPagesIndex = currentDisplay;
        } else if (currentAuthoredPage >= 0) {
            // Stale fragment (e.g. split changed with the locale): fall back to the
            // first fragment of the authored page.
            this.openPagesIndex = this.getOpenPagesIndexForPage(currentAuthoredPage);
        }
        if (this.displayPages.isEmpty()) {
            this.openPagesIndex = 0;
        } else {
            this.openPagesIndex = Math.max(0, Math.min(this.openPagesIndex, this.displayPages.size() - 1));
        }
    }

    /**
     * @return the body text Y used for first-fragment measurement, or {@link Integer#MIN_VALUE}
     * when the page does not support splitting.
     */
    private int getSplitTextY(BookPage page) {
        if (!PageSplitter.isSplitEnabled(page)) {
            return Integer.MIN_VALUE;
        }
        try {
            var renderer = PageRendererRegistry.getPageRenderer(page.getType()).create(page);
            if (renderer instanceof PageWithTextRenderer withText) {
                return withText.getTextY();
            }
        } catch (Exception e) {
            Modonomicon.LOG.warn("Failed to resolve text bounds for page splitting: {}", page.getId(), e);
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Rebuilds the display list when the locale, font, or fallback-font state changed
     * since the last build, then refreshes the visible pages.
     *
     * @return true if a rebuild happened.
     */
    protected boolean rebuildDisplayPagesIfStale() {
        var minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return false;
        }
        var locale = minecraft.getLanguageManager() != null ? minecraft.getLanguageManager().getSelected() : "";
        var font = minecraft.font;
        var reloadGeneration = BookDataManager.Client.get().reloadGeneration();
        if (!this.displayPages.isEmpty()
                && locale.equals(this.lastSplitLocale)
                && font == this.lastSplitFont
                && reloadGeneration == this.lastSplitReloadGeneration) {
            return false;
        }
        this.rebuildDisplayPages();
        this.onPageChanged();
        return true;
    }

    public void setOpenPagesIndex(int openPagesIndex) {
        this.openPagesIndex = openPagesIndex;
    }

    /**
     * Will change to the specified authored page, if not open already.
     * A link to a split page always opens its first display fragment.
     */
    public void goToPage(int pageIndex, boolean playSound) {
        if (this.displayPages.isEmpty()) {
            this.rebuildDisplayPages();
        }
        int openPagesIndex = this.getOpenPagesIndexForPage(pageIndex);
        if (openPagesIndex >= 0 && openPagesIndex < this.displayPages.size()) {
            if (this.openPagesIndex != openPagesIndex) {
                this.openPagesIndex = openPagesIndex;

                this.onPageChanged();
                if (playSound) {
                    BookContentRenderer.playTurnPageSound(this.getBook());
                }
            }
        } else {
            Modonomicon.LOG.warn("Tried to change to page index {} corresponding with " +
                    "openPagesIndex {} but max open pages index is {}.", pageIndex, openPagesIndex, this.displayPages.size());
        }
    }

    /**
     * @return the transient virtual display index of the currently shown fragment.
     * This is only meaningful while the display list is unchanged (same locale/font).
     */
    public int getCurrentDisplayPageIndex() {
        if (this.displayPages.isEmpty()) {
            return 0;
        }
        return Math.max(0, Math.min(this.openPagesIndex, this.displayPages.size() - 1));
    }

    /**
     * Will change to the specified virtual display fragment, if it still belongs to the
     * expected authored page. Used for back-history navigation where the split is
     * unchanged. Otherwise falls back to the first fragment of the authored page.
     */
    public void goToDisplayPage(int displayIndex, int expectedAuthoredPage, boolean playSound) {
        if (this.displayPages.isEmpty()) {
            this.rebuildDisplayPages();
        }
        if (displayIndex >= 0 && displayIndex < this.displayPages.size()
                && this.displayPages.get(displayIndex).page().getPageNumber() == expectedAuthoredPage) {
            if (this.openPagesIndex != displayIndex) {
                this.openPagesIndex = displayIndex;

                this.onPageChanged();
                if (playSound) {
                    BookContentRenderer.playTurnPageSound(this.getBook());
                }
            }
            return;
        }

        Modonomicon.LOG.debug("Display index {} no longer belongs to authored page {}, falling back to its first fragment.",
                displayIndex, expectedAuthoredPage);
        this.goToPage(expectedAuthoredPage, playSound);
    }

    protected Style getClickedComponentStyleAtForPage(BookPageRenderer<?> page, double pMouseX, double pMouseY) {
        if (page != null) {
            return page.getClickedComponentStyleAt(pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top);
        }

        return null;
    }

    public void removeRenderableWidgets(@NotNull Collection<? extends Renderable> renderables) {
        this.renderables.removeIf(renderables::contains);
        this.children().removeIf(c -> c instanceof Renderable && renderables.contains(c));
        this.narratables.removeIf(n -> n instanceof Renderable && renderables.contains(n));
    }

    protected void drawTooltip(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltipStack != null) {
            List<Component> tooltip = this.getTooltipFromItem(this.tooltipStack);
            var tooltipImage = this.tooltipStack.getTooltipImage();
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, tooltipImage, pMouseX, pMouseY);
        } else if (this.tooltipFluidStack != null) {
            List<Component> tooltip = this.getTooltipFromFluid(this.tooltipFluidStack);
            guiGraphics.setTooltipForNextFrame(tooltip.stream().map(Component::getVisualOrderText).toList(), pMouseX, pMouseY);
        } else if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.setTooltipForNextFrame(this.tooltip.stream().map(Component::getVisualOrderText).toList(), pMouseX, pMouseY);
        }
    }

    protected boolean clickPage(BookPageRenderer<?> page, MouseButtonEvent event, boolean isDoubleClick) {
        if (page != null) {
            var localEvent = new MouseButtonEvent(event.x() - this.bookLeft - page.left, event.y() - this.bookTop - page.top, event.buttonInfo());
            return page.mouseClicked(localEvent, isDoubleClick);
        }

        return false;
    }

    protected boolean clickIngredientPage(BookPageRenderer<?> page, MouseButtonEvent event) {
        if (page != null) {
            var localEvent = new MouseButtonEvent(event.x() - this.bookLeft - page.left, event.y() - this.bookTop - page.top, event.buttonInfo());
            return page.mouseClickedIngredient(localEvent);
        }

        return false;
    }

    protected void renderPage(GuiGraphicsExtractor guiGraphics, BookPageRenderer<?> page, int pMouseX, int pMouseY, float pPartialTick) {
        if (page == null) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(page.left, page.top);
        page.render(guiGraphics, pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top, pPartialTick);
        if (BookDebugOverlay.isEnabled()) {
            page.renderDebugOverlay(guiGraphics, pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top);
        }
        guiGraphics.pose().popMatrix();
    }

    protected void onPageChanged() {
        this.beginDisplayPages();
    }

    protected void resetTooltip() {
        this.tooltip = null;
        this.tooltipStack = null;
        this.tooltipFluidStack = null;
    }

    public void loadState(EntryVisualState state) {
        this.openPagesIndex = state.openPagesIndex;
        if (!this.displayPages.isEmpty()) {
            this.openPagesIndex = Math.max(0, Math.min(this.openPagesIndex, this.displayPages.size() - 1));
        }
    }

    public void saveState(EntryVisualState state, boolean savePage) {
        state.openPagesIndex = savePage ? this.openPagesIndex : 0;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        //do not call super, as it would close the screen stack
        //In most cases closeEntryScreen should be called directly, but if our parent BookPaginatedScreen wants us to close we need to handle that
        BookGuiManager.get().closeEntryScreen(this);
    }

    /**
     * Make public to access from pages
     */
    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    public List<Component> getTooltipFromItem(ItemStack pItemStack) {
        var tooltip = getTooltipFromItem(Minecraft.getInstance(), pItemStack);

        if (ClientServices.CLIENT_CONFIG.showRecipeLookupHints()) {
            //Any item rendered in the book can be clicked to look up its recipes/usages, so show the hint whenever a viewer is available.
            if (RecipeViewerRegistry.isAnyAvailable()) {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GREEN)));
            } else if (this.isHoveringItemLink()) {
                //item links are only clickable when a viewer is available, so explain the requirement
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_NO_RECIPE_VIEWER).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.RED)));
            }
        }

        return tooltip;
    }

    public List<Component> getTooltipFromFluid(FluidHolder fluidStack) {
        var tooltip = ClientServices.FLUID.getTooltip(fluidStack, FluidHolder.BUCKET_VOLUME, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL, FluidHelper.TooltipMode.SHOW_AMOUNT_AND_CAPACITY);

        if (this.isHoveringItemLink() && ClientServices.CLIENT_CONFIG.showRecipeLookupHints()) {
            tooltip.add(Component.literal(""));
            if (RecipeViewerRegistry.isAnyAvailable()) {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GREEN)));
            } else {
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_NO_RECIPE_VIEWER).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.RED)));
            }
        }

        return tooltip;
    }

    //TODO: check if we need to change this to the new click style detection stuff
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        if (pStyle != null) {
            for (LinkHandler handler : this.linkHandlers) {
                var result = handler.handleClick(pStyle);

                //before the command pattern was implemented we returned false for failures
                //however, I believe failure should also be treated as "handled" to avoid vanilla code doing fun stuff.
                //We retain the failure result in case that turns out to be wrong
                if (result == LinkHandler.ClickResult.FAILURE)
                    return true;

                if (result == LinkHandler.ClickResult.SUCCESS)
                    return true;

                //unhandled -> continue to next
            }
        }
        return false;
    }

    @Override
    protected void init() {
        super.init();

        this.unlockedPages = this.entry.getUnlockedPagesFor(this.minecraft.player);
        this.rebuildDisplayPages();
        this.beginDisplayPages();
    }

    @Override
    protected void initNavigationButtons() {
        super.initNavigationButtons();

        this.addRenderableWidget(new BackButton(this, this.width / 2 - BackButton.WIDTH / 2, this.bookTop + FULL_HEIGHT - BackButton.HEIGHT / 2));

        this.updateBookmarksButton();
    }

    protected boolean isBookmarked() {
        return BookVisualStateManager.get().getBookmarksFor(this.minecraft.player, this.entry.getBook()).stream().anyMatch(b -> b.entryId().equals(this.entry.getId()));
    }

    protected void updateBookmarksButton() {
        this.renderables.removeIf(b -> b instanceof AddBookmarkButton || b instanceof RemoveBookmarkButton);
        this.children().removeIf(b -> b instanceof AddBookmarkButton || b instanceof RemoveBookmarkButton);
        this.narratables.removeIf(b -> b instanceof AddBookmarkButton || b instanceof RemoveBookmarkButton);

        int buttonHeight = this.getBook().theme().content().addBookmarkButton().normal().height();
        int searchButtonY = this.bookTop + FULL_HEIGHT - 30;
        int searchButtonWidth = this.getBook().theme().content().addBookmarkButton().normal().width();
        int scissorX = this.bookLeft + FULL_WIDTH;//this is the render location of our frame so our search button never overlaps
        int searchButtonX = BookSideButtonRenderer.anchoredButtonX(scissorX);


        if (this.isBookmarked()) {
            var removeBookMarkButton = new RemoveBookmarkButton(this, searchButtonX, searchButtonY,
                    scissorX,
                    searchButtonWidth, buttonHeight,
                    (b) -> this.onRemoveBookmarksButtonClick((RemoveBookmarkButton) b),
                    Tooltip.create(Component.translatable(Gui.REMOVE_BOOKMARK)));
            this.addRenderableWidget(removeBookMarkButton);
        } else {
            var addBookmarkButton = new AddBookmarkButton(this, searchButtonX, searchButtonY,
                    scissorX,
                    searchButtonWidth, buttonHeight,
                    (b) -> this.onAddBookmarksButtonClick((AddBookmarkButton) b),
                    Tooltip.create(Component.translatable(Gui.ADD_BOOKMARK)));
            this.addRenderableWidget(addBookmarkButton);
        }

    }

    protected void onAddBookmarksButtonClick(AddBookmarkButton button) {
        if (!this.isBookmarked()) {
            var bookmarkAddress = BookAddress.ignoreSaved(this.entry, this.getPageForOpenPagesIndex(this.openPagesIndex));
            BookVisualStateManager.get().addBookmarkFor(this.minecraft.player, this.entry.getBook(), bookmarkAddress);

            Services.NETWORK.sendToServer(new AddBookmarkMessage(bookmarkAddress));

            this.updateBookmarksButton();
        }
    }

    protected void onRemoveBookmarksButtonClick(RemoveBookmarkButton button) {
        //no need to check for is bookmarked because we query the bookmark in question directly anyway
        var bookmarkAddress = BookVisualStateManager.get().getBookmarksFor(this.minecraft.player, this.entry.getBook()).stream().filter(b -> b.entryId().equals(this.entry.getId())).findFirst().orElse(null);
        if (bookmarkAddress != null) {
            BookVisualStateManager.get().removeBookmarkFor(this.minecraft.player, this.entry.getBook(), bookmarkAddress);

            this.updateBookmarksButton();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getMinecraft().hasShiftDown()) {
            this.ticksInBook++;
        }

        this.rebuildDisplayPagesIfStale();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            var style = this.getClickedComponentStyleAt(event.x(), event.y());
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        //Any item rendered on a page can be clicked to look up its recipes/usages.
        //Left-click shows recipes, right-click (or shift+left-click) shows usages.
        if (this.tooltipStack != null && !this.tooltipStack.isEmpty() && RecipeViewerRegistry.isAnyAvailable()) {
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                this.lookupItemStack(this.tooltipStack, this.getMinecraft().hasShiftDown());
                return true;
            }
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                this.lookupItemStack(this.tooltipStack, true);
                return true;
            }
        }

        //Let the page handle clicks on non-item ingredients (e.g. fluids) before generic right-click navigation.
        if (this.mouseClickedPageIngredient(event, isDoubleClick)) {
            return true;
        }

        if (super.mouseClicked(event, isDoubleClick)) {
            return true;
        }

        return this.mouseClickedPage(event, isDoubleClick);
    }

    private void lookupItemStack(ItemStack stack, boolean uses) {
        BookFeedback.showLookup(stack, uses);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void onSyncBookVisualStatesMessage(SyncBookVisualStatesMessage message) {
        this.updateBookmarksButton();
    }

    /**
     * Re-evaluates page visibility against the newly synced research state.
     * Called when the server pushes research state while this entry is open,
     * so pages gated on just-unlocked (or just-relocked) conditions swap
     * without closing and reopening the entry.
     */
    public void onSyncResearchStateMessage(SyncResearchStateMessage message) {
        this.unlockedPages = this.entry.getUnlockedPagesFor(this.minecraft.player);
        this.rebuildDisplayPages();
        this.onPageChanged();
    }

    @Override
    public Font getContentFont() {
        //this is necessary because while Screen has getFont(), if a mod uses non-mojang mappings the method won't be found
        return this.font;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //do not render background because we are on a gui stack and double blur would crash
    }

    protected abstract int getOpenPagesIndexForPage(int pageIndex);

    /**
     * Gets the page index for the first page to display for the given open pages index.
     */
    protected abstract int getPageForOpenPagesIndex(int openPagesIndex);

    @Nullable
    protected abstract Style getClickedComponentStyleAt(double pMouseX, double pMouseY);

    protected abstract boolean mouseClickedPage(MouseButtonEvent event, boolean isDoubleClick);

    /**
     * Lets the displayed page(s) handle clicks on non-item ingredients before generic click handling.
     * <p>
     * Defaults to false, subclasses with page renderers should override this and delegate to
     * {@link #clickIngredientPage(BookPageRenderer, MouseButtonEvent)}.
     */
    protected boolean mouseClickedPageIngredient(MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }

    protected abstract void beginDisplayPages();
}
