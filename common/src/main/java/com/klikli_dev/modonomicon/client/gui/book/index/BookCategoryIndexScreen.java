/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.index;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.api.events.EntryClickedEvent;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedListScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

public class BookCategoryIndexScreen extends BookPaginatedListScreen<BookEntry> implements BookCategoryScreen {
    protected final BookParentScreen parentScreen;
    protected final BookCategory category;

    public BookCategoryIndexScreen(BookParentScreen parentScreen, BookCategory category) {
        this(parentScreen, category, true);
    }

    public BookCategoryIndexScreen(BookParentScreen parentScreen, BookCategory category, boolean addExitButton) {
        super(Component.translatable(category.getName()), addExitButton);
        this.parentScreen = parentScreen;
        this.category = category;
    }

    @Override
    public void handleButtonEntry(Button button) {
        var entry = ((EntryListButton) button).getEntry();

        var displayStyle = this.getEntryDisplayState(entry);
        var event = new EntryClickedEvent(this.category.getBook().getId(), entry.getId(), new MouseButtonEvent(button.getX(), button.getY(), new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_1, 0)), displayStyle);

        //if event is canceled -> click was handled and we do not open the entry.
        if (ModonomiconEvents.client().entryClicked(event)) {
            return;
        }

        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
    }

    @Override
    protected void openOnlyVisibleEntry() {
        var entry = this.visibleEntries.get(0);
        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
    }

    @Override
    protected boolean fillLeftPageOnFirstPage() {
        //the description takes the left page, so the list starts on the right
        return !this.shouldShowDescription();
    }

    @Override
    protected List<BookEntry> computeAllEntries() {
        //we filter out entries that are locked or in locked categories
        //TODO: should we NOT filter out locked but visible entries and display them with a lock or greyed out?
        // + tooltip?
        return this.category.getEntries().values().stream().filter(e ->
                        BookServices.visibility().isVisible(this.minecraft.player, e.getCategory()) &&
                                BookServices.visibility().isAccessible(this.minecraft.player, e)
                ).sorted(Comparator.comparingInt(BookEntry::getSortNumber)
                        .thenComparing(a -> I18n.get(a.getName())))
                .toList();
    }

    @Override
    protected Button createEntryButton(BookEntry entry, int x, int y) {
        var button = new EntryListButton(entry, x, y, this::handleButtonEntry);
        button.setTooltip(Tooltip.create(Component.translatable(entry.getDescription())));
        return button;
    }

    @Override
    @Nullable
    protected LinkedText linkedText() {
        if (this.openPagesIndex == 0 && this.shouldShowDescription()) {
            return new LinkedText(this.category.getDescription(),
                    BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                    true);
        }
        return null;
    }

    @Override
    public Identifier categoryId() {
        return this.category.getId();
    }

    @Override
    public void closeForExternalNavigation() {
        BookGuiManager.get().closeScreenStack(this);
    }

    protected void drawTitle(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.pose().pushMatrix();
        var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(this.getTitle()));
        if (scale < 1) {
            guiGraphics.pose().translate(x - x * scale, y - y * scale);
            guiGraphics.pose().scale(scale, scale);
        }

        //we use scale 1 because our scale translation handling in there is off a bit. the above translation code is better
        this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(), x, y, this.getBook().theme().palette().defaultTitleColor(), 1);
        guiGraphics.pose().popMatrix();
    }

    public BookParentScreen getParentScreen() {
        return this.parentScreen;
    }

    protected boolean shouldShowDescription() {
        return !this.category.getDescription().isEmpty();
    }

    @Override
    public Book getBook() {
        return this.parentScreen.getBook();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //do not render background because we are on a gui stack and double blur would crash
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (BookGuiManager.get().openBookEntryScreen != null) //do not render self while an entry screen is open to avoid double render effects
            return;

        this.resetTooltip();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);

        BookContentRenderer.renderBookBackground(guiGraphics, this.getBook());


        if (this.openPagesIndex == 0) {
            if (!this.shouldShowDescription()) {

                this.drawTitle(guiGraphics, BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING);

                BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            } else {
                this.drawTitle(guiGraphics, BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING);
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.CATEGORY_INDEX_LIST_TITLE),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.parentScreen.getBook().theme().palette().defaultTitleColor());

                BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
                BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);


                BookPageRenderer.renderIndexBookTextHolder(guiGraphics, this.category.getDescription(), this.font, this.parentScreen.getBook().theme().layout(),
                        BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                        this.parentScreen.getBook().theme().palette().defaultTextColor());
            }
        }

        guiGraphics.pose().popMatrix();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //hover tooltips for links in the category description, if any
        this.renderLinkedTextHover(guiGraphics, pMouseX, pMouseY);

        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }


    @Override
    public void onDisplay() {

    }

    @Override
    public void onClose() {
        //do not call super, as it would close the screen stack
        //In most cases closeEntryScreen should be called directly, but if our parent BookPaginatedScreen wants us to close we need to handle that
        BookGuiManager.get().closeCategoryScreen(this);
    }

    @Override
    public void loadState(CategoryVisualState state) {
        this.openPagesIndex = state.openPagesIndex;
    }

    @Override
    public void saveState(CategoryVisualState state) {
        state.openPagesIndex = this.openPagesIndex;
    }

    @Override
    public BookCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                this.openOnlyVisibleEntry();
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public void init() {
        super.init();

        this.allEntries = this.computeAllEntries();

        this.createEntryList();
    }
}
