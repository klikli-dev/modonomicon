// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler.LinkClickContext;
import com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler.LinkClickDispatcher;
import com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler.LinkHandler;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared foundation for the paginated list screens: category index, parent index, search, bookmarks and
 * recently unlocked.
 * <p>
 * Unifies pagination, entry buttons, tooltips, titles and - most importantly - click and hover handling for
 * linked texts (category and book descriptions, info texts). Subclasses only declare their entries, buttons
 * and the optional interactive text; hit-testing, hover tooltips and click dispatching work the same everywhere.
 */
public abstract class BookPaginatedListScreen<E> extends BookPaginatedScreen implements LinkClickContext, BookTextInteraction.HoverContext {

    public static final int ENTRIES_PER_PAGE = 11;
    public static final int ENTRIES_IN_FIRST_PAGE = 9;

    protected final List<Button> entryButtons = new ArrayList<>();
    protected final List<E> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    protected int openPagesIndex;
    protected int maxOpenPagesIndex;
    protected List<E> allEntries = List.of();
    protected List<Component> tooltip;

    private List<LinkHandler> linkHandlers;
    private boolean hoveringItemLink;

    public BookPaginatedListScreen(Component component) {
        super(component);
    }

    public BookPaginatedListScreen(Component component, boolean addExitButton) {
        super(component, addExitButton);
    }

    //region list content

    /**
     * Computes the full backing list of entries. Called from {@link #init()}.
     */
    protected abstract List<E> computeAllEntries();

    /**
     * Creates the button opening the given list entry.
     */
    protected abstract Button createEntryButton(E entry, int x, int y);

    public abstract void handleButtonEntry(Button button);

    /**
     * Opens the only visible entry, used for enter-key handling. {@link #visibleEntries} is guaranteed
     * to hold exactly one element when this is called.
     */
    protected abstract void openOnlyVisibleEntry();

    /**
     * Narrows {@link #allEntries} down to {@link #visibleEntries}. Defaults to showing everything,
     * the search screen filters by query instead.
     */
    protected void filterEntries() {
        this.visibleEntries.addAll(this.allEntries);
    }

    /**
     * Whether the left page of the first page pair shows list entries. Index screens return false here
     * while they show their description, all other screens always fill both pages.
     */
    protected abstract boolean fillLeftPageOnFirstPage();

    protected int entriesPerPage() {
        return ENTRIES_PER_PAGE;
    }

    protected int entriesInFirstPage() {
        return ENTRIES_IN_FIRST_PAGE;
    }

    protected int getEntryCountStart() {
        if (this.openPagesIndex == 0) {
            return 0;
        }

        int start = this.entriesInFirstPage();
        start += (this.entriesPerPage() * 2) * (this.openPagesIndex - 1);
        return start;
    }

    protected void createEntryList() {
        this.entryButtons.forEach(b -> {
            this.renderables.remove(b);
            this.children().remove(b);
            this.narratables.remove(b);
        });

        this.entryButtons.clear();
        this.visibleEntries.clear();

        this.filterEntries();

        this.maxOpenPagesIndex = 1;
        int count = this.visibleEntries.size();
        count -= this.entriesInFirstPage();
        if (count > 0) {
            this.maxOpenPagesIndex += (int) Math.ceil((float) count / (this.entriesPerPage() * 2));
        }

        while (this.getEntryCountStart() > this.visibleEntries.size()) {
            this.openPagesIndex--;
        }

        if (this.openPagesIndex == 0) {
            if (this.fillLeftPageOnFirstPage()) {
                this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 20, 0, this.entriesInFirstPage());
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, this.entriesInFirstPage(), this.entriesPerPage());
            } else {
                //descriptions and info texts take the left page, so the list starts on the right
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING + 20, 0, this.entriesInFirstPage());
            }
        } else {
            int start = this.getEntryCountStart();
            this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING, start, this.entriesPerPage());
            this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, start + this.entriesPerPage(), this.entriesPerPage());
        }
    }

    protected void addEntryButtons(int x, int y, int start, int count) {
        for (int i = 0; i < count && (i + start) < this.visibleEntries.size(); i++) {
            var button = this.createEntryButton(this.visibleEntries.get(start + i), this.bookLeft + x, this.bookTop + y + i * 13);
            this.addRenderableWidget(button);
            this.entryButtons.add(button);
        }
    }

    protected void onPageChanged() {
        this.createEntryList();
    }

    @Override
    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    @Override
    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {

            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                BookContentRenderer.playTurnPageSound(this.getBook());
            }
        }
    }

    //endregion
    //region tooltip & titles

    @Override
    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    protected void drawTooltip(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.setTooltipForNextFrame(this.tooltip.stream().map(Component::getVisualOrderText).toList(), pMouseX, pMouseY);
        }
    }

    protected void resetTooltip() {
        this.tooltip = null;
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, Component s, int x, int y, int color) {
        this.drawCenteredStringNoShadow(guiGraphics, s, x, y, color, 1.0f);
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, Component s, int x, int y, int color, float scale) {
        TextRenderHelper.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    //endregion
    //region linked texts (descriptions, info texts)

    /**
     * An interactive text rendered by this screen, e.g. a category description or an info text.
     *
     * @param indexOffset whether the theme's index text offset applies (descriptions rendered via
     *                    {@code BookPageRenderer.renderIndexBookTextHolder}, as opposed to plain info texts).
     */
    public record LinkedText(BookTextHolder text, int x, int y, int width, int height, boolean indexOffset) {
    }

    /**
     * The interactive text currently displayed, or {@code null} if none (e.g. on later page pairs).
     * Coordinates are relative to the book content origin (before the bookLeft/bookTop translation).
     */
    @Nullable
    protected abstract LinkedText linkedText();

    /**
     * Hit-tests the current {@link #linkedText()} with the exact bounds and offsets used for rendering,
     * so links highlight where they are drawn.
     *
     * @param mouseX screen coordinates
     * @param mouseY screen coordinates
     */
    @Nullable
    protected Style getLinkedTextStyleAt(double mouseX, double mouseY) {
        var linked = this.linkedText();
        if (linked == null || linked.text() == null || linked.text().isEmpty())
            return null;

        //rendering translates the pose by bookLeft/bookTop, so test in that same space
        double localX = mouseX - this.bookLeft;
        double localY = mouseY - this.bookTop;

        int x = linked.x();
        int y = linked.y();
        int width = linked.width();
        int height = linked.height();
        if (linked.indexOffset()) {
            var layout = this.getBook().theme().layout();
            var bounds = BookPageRenderer.applyTextOffset(layout.indexTextOffsetX(), layout.indexTextOffsetY(), layout.indexTextOffsetWidth(), layout.indexTextOffsetHeight(), x, y, width, height);
            x = bounds.x;
            y = bounds.y;
            width = bounds.width;
            height = bounds.height;
        }

        return BookTextInteraction.hitTestTextHolder(this.font, null, this.getBook(), linked.text(), x, y, width, height, localX, localY);
    }

    /**
     * Renders the hover tooltip for the current {@link #linkedText()}, if any. Call with screen coordinates
     * after rendering the text. Mirrors how page renderers hover their texts.
     */
    protected void renderLinkedTextHover(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        var style = this.getLinkedTextStyleAt(mouseX, mouseY);
        if (style != null) {
            BookTextInteraction.renderHover(this, guiGraphics, style, mouseX, mouseY);
        }
    }

    /**
     * Clicks the current {@link #linkedText()}, if any. Mirrors entry screen click handling:
     * our link handlers first, then vanilla handling for the remaining click actions.
     */
    protected boolean clickLinkedText(MouseButtonEvent event) {
        var style = this.getLinkedTextStyleAt(event.x(), event.y());
        return style != null && LinkClickDispatcher.dispatch(this, this.linkHandlers(), style,
                clickEvent -> Screen.defaultHandleGameClickEvent(clickEvent, this.minecraft, this));
    }

    protected List<LinkHandler> linkHandlers() {
        if (this.linkHandlers == null) {
            this.linkHandlers = LinkClickDispatcher.defaultHandlers(this);
        }
        return this.linkHandlers;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        //linked texts take priority, just like on entry pages
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.clickLinkedText(event)) {
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    //endregion
    //region LinkClickContext

    @Override
    public Identifier entryId() {
        //list screens render no entry, so there is no entry context for command links
        return null;
    }

    @Override
    public Identifier categoryId() {
        return null;
    }

    //endregion
    //region HoverContext

    @Override
    public Book book() {
        return this.getBook();
    }

    @Override
    public Font font() {
        return this.font;
    }

    @Override
    public int screenWidth() {
        return this.width;
    }

    @Override
    public void screenWidth(int width) {
        this.width = width;
    }

    @Override
    public List<Component> itemTooltip(ItemStack stack) {
        var tooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);

        if (ClientServices.CLIENT_CONFIG.showRecipeLookupHints()) {
            //Any item rendered in the book can be clicked to look up its recipes/usages, so show the hint whenever a viewer is available.
            if (RecipeViewerRegistry.isAnyAvailable()) {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GREEN)));
            } else if (this.hoveringItemLink) {
                //item links are only clickable when a viewer is available, so explain the requirement
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable(Gui.HOVER_ITEM_LINK_INFO_NO_RECIPE_VIEWER).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.RED)));
            }
        }

        return tooltip;
    }

    @Override
    public void hoveringItemLink(boolean value) {
        this.hoveringItemLink = value;
    }

    //endregion
}
