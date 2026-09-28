/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.search;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedListScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.platform.ClientServices;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

public class BookSearchScreen extends BookPaginatedListScreen<BookEntry> {
    protected final BookParentScreen parentScreen;
    private EditBox searchField;
    private BookTextHolder infoText;

    public BookSearchScreen(BookParentScreen parentScreen) {
        super(Component.translatable(Gui.SEARCH_SCREEN_TITLE));
        this.parentScreen = parentScreen;

        this.infoText = new BookTextHolder(Gui.SEARCH_INFO_TEXT);
    }

    @Override
    public void handleButtonEntry(Button button) {
        var entry = ((EntryListButton) button).getEntry();
        this.onClose();
        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
    }

    @Override
    protected void openOnlyVisibleEntry() {
        var entry = this.visibleEntries.get(0);
        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
    }

    @Override
    protected boolean fillLeftPageOnFirstPage() {
        //the info text takes the left page, so the list starts on the right
        return false;
    }

    @Override
    protected List<BookEntry> computeAllEntries() {
        //we filter out entries that are locked or in locked categories
        //TODO: should we NOT filter out locked but visible entries and display them with a lock?
        return this.getEntries().stream().filter(e ->
                BookServices.visibility().isVisible(this.minecraft.player, e.getCategory()) &&
                        BookServices.visibility().isAccessible(this.minecraft.player, e)
        ).sorted(Comparator.comparing(a -> I18n.get(a.getName()))).toList();
    }

    private List<BookEntry> getEntries() {
        return this.parentScreen.getBook().getEntries().values().stream().toList();
    }

    @Override
    protected void filterEntries() {
        String query = this.searchField.getValue().toLowerCase();
        this.allEntries.stream().filter((e) -> e.matchesQuery(query, this.minecraft.level)).forEach(this.visibleEntries::add);
    }

    @Override
    protected Button createEntryButton(BookEntry entry, int x, int y) {
        return new EntryListButton(entry, x, y, this::handleButtonEntry);
    }

    @Override
    @Nullable
    protected LinkedText linkedText() {
        if (this.openPagesIndex == 0) {
            return new LinkedText(this.infoText,
                    BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                    false);
        }
        return null;
    }

    @Override
    public void closeForExternalNavigation() {
        ClientServices.GUI.popGuiLayer();
    }

    public void prerenderMarkdown(BookTextRenderer textRenderer) {

        if (!this.infoText.hasComponent()) {
            this.infoText = new RenderedBookTextHolder(this.infoText, textRenderer.render(this.infoText.getString()));
        }
    }

    public BookParentScreen getParentScreen() {
        return this.parentScreen;
    }

    private void createSearchBar() {
        this.searchField = new EditBox(this.font, 160, 170, 90, 12, Component.literal(""));
        this.searchField.setMaxLength(32);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setFocused(true);
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
        this.resetTooltip();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);

        BookContentRenderer.renderBookBackground(guiGraphics, this.getBook());


        if (this.openPagesIndex == 0) {
            this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                    BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                    this.parentScreen.getBook().theme().palette().defaultTitleColor());
            this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_ENTRY_LIST_TITLE),
                    BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                    this.parentScreen.getBook().theme().palette().defaultTitleColor());

            BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                    BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                    BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);

            BookPageRenderer.renderBookTextHolder(guiGraphics, this.infoText, this.font,
                    BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                    this.parentScreen.getBook().theme().palette().defaultTextColor());
        }


        if (!this.searchField.getValue().isEmpty()) {
            //draw search field bg
            BookContentRenderer.drawSprite(guiGraphics, this.parentScreen.getBook().theme().content().searchFieldBackground(), this.searchField.getX() - 8, this.searchField.getY());
            var searchComponent = Component.literal(this.searchField.getValue());
            guiGraphics.text(this.font, searchComponent, this.searchField.getX() + 7, this.searchField.getY() + 1, 0xFF000000, false);
        }

        if (this.visibleEntries.isEmpty()) {
            if (!this.searchField.getValue().isEmpty()) {
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS), BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, 80, 0xFF333333);
                guiGraphics.pose().scale(2F, 2F);
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS_SAD), BookEntryScreen.RIGHT_PAGE_X / 2 + BookEntryScreen.PAGE_WIDTH / 4, 47, 0xFF999999);
                guiGraphics.pose().scale(0.5F, 0.5F);
            } else {
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS), BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, 80, 0xFF333333);
            }
        }
        guiGraphics.pose().popMatrix();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //hover tooltips for links in the info text, if any
        this.renderLinkedTextHover(guiGraphics, pMouseX, pMouseY);

        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }


    @Override
    public void onClose() {
        //Search screen is not supposed to close everything on Esc, so we just pop a layer.
        ClientServices.GUI.popGuiLayer();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        String currQuery = this.searchField.getValue();

        if (event.key() == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                this.openOnlyVisibleEntry();
                return true;
            }
        } else if (this.searchField.keyPressed(event)) {
            if (!this.searchField.getValue().equals(currQuery)) {
                this.createEntryList();
            }

            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    protected boolean shouldCloseOnInventoryKey() {
        //while searching (search field focused) the inventory key must type into the search instead of closing,
        //mirroring vanilla creative inventory search behaviour
        return this.searchField == null || !this.searchField.isFocused();
    }

    @Override
    public void init() {
        super.init();

        var textRenderer = new BookTextRenderer(this.getBook(), this.minecraft.level.registryAccess());
        this.prerenderMarkdown(textRenderer);

        this.allEntries = this.computeAllEntries();

        this.createSearchBar();
        this.createEntryList();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (super.mouseClicked(event, isDoubleClick)) {
            return true;
        }
        var localEvent = new MouseButtonEvent(event.x() - this.bookLeft, event.y() - this.bookTop, event.buttonInfo());
        return this.searchField.mouseClicked(localEvent, isDoubleClick);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String currQuery = this.searchField.getValue();
        if (this.searchField.charTyped(event)) {
            if (!this.searchField.getValue().equals(currQuery)) {
                this.createEntryList();
            }

            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
