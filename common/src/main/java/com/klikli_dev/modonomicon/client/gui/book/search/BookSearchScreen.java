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
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookSearchScreen extends BookPaginatedScreen {
    public static final int ENTRIES_PER_PAGE = 13;
    public static final int ENTRIES_IN_FIRST_PAGE = 11;
    protected final List<Button> entryButtons = new ArrayList<>();
    protected final BookParentScreen parentScreen;
    private final List<BookEntry> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<BookEntry> allEntries;
    private EditBox searchField;
    private BookTextHolder infoText;
    private List<Component> tooltip;

    public BookSearchScreen(BookParentScreen parentScreen) {
        super(Component.translatable(Gui.SEARCH_SCREEN_TITLE));
        this.parentScreen = parentScreen;

        this.infoText = new BookTextHolder(Gui.SEARCH_INFO_TEXT);
    }

    public void handleButtonEntry(Button button) {
        var entry = ((EntryListButton) button).getEntry();
        this.onClose();
        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
    }

    public void prerenderMarkdown(BookTextRenderer textRenderer) {

        if (!this.infoText.hasComponent()) {
            this.infoText = new RenderedBookTextHolder(this.infoText, textRenderer.render(this.infoText.getString()));
        }
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color) {
        this.drawCenteredStringNoShadow(guiGraphics, s, x, y, color, 1.0f);
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color, float scale) {
        GuiGraphicsExt.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public BookParentScreen getParentScreen() {
        return this.parentScreen;
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
                BookContentRenderer.playTurnPageSound(this.parentScreen.getBook());
            }
        }
    }


    protected void drawTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.renderComponentTooltip(this.font, this.tooltip, pMouseX, pMouseY);
        }
    }

    protected void onPageChanged() {
        this.createEntryList();
    }

    protected void resetTooltip() {
        this.tooltip = null;
    }

    private void createSearchBar() {
        this.searchField = new EditBox(this.font, 160, 170, 90, 12, Component.literal(""));
        this.searchField.setMaxLength(32);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setFocused(true);
    }

    private void createEntryList() {
        this.entryButtons.forEach(b -> {
            this.renderables.remove(b);
            this.children().remove(b);
            this.narratables.remove(b);
        });

        this.entryButtons.clear();
        this.visibleEntries.clear();

        String query = this.searchField.getValue().toLowerCase();
        this.allEntries.stream().filter((e) -> e.matchesQuery(query)).forEach(this.visibleEntries::add);

        this.maxOpenPagesIndex = 1;
        int count = this.visibleEntries.size();
        count -= ENTRIES_IN_FIRST_PAGE;
        if (count > 0) {
            this.maxOpenPagesIndex += (int) Math.ceil((float) count / (ENTRIES_PER_PAGE * 2));
        }

        while (this.getEntryCountStart() > this.visibleEntries.size()) {
            this.openPagesIndex--;
        }

        if (this.openPagesIndex == 0) {
            //only show on the right for the first page
            this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
        } else {
            int start = this.getEntryCountStart();
            this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING, start, ENTRIES_PER_PAGE);
            this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, start + ENTRIES_PER_PAGE, ENTRIES_PER_PAGE);
        }
    }

    private int getEntryCountStart() {
        if (this.openPagesIndex == 0) {
            return 0;
        }

        int start = ENTRIES_IN_FIRST_PAGE;
        start += (ENTRIES_PER_PAGE * 2) * (this.openPagesIndex - 1);
        return start;
    }

    private List<BookEntry> getEntries() {
        return this.parentScreen.getBook().getEntries().values().stream().toList();
    }

    @Override
    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Book getBook() {
        return this.parentScreen.getBook();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest(); //guard against depth test being enabled by other rendering code, that would cause ui elements to vanish

        this.resetTooltip();

        //we need to modify blit offset (now: z pose) to not draw over toasts
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -1300);  //magic number arrived by testing until toasts show, but BookOverviewScreen does not
        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);

        BookContentRenderer.renderBookBackground(guiGraphics, this.getBook().getBookContentTexture());


        if (this.openPagesIndex == 0) {
            this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                    BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                    this.parentScreen.getBook().getDefaultTitleColor());
            this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_ENTRY_LIST_TITLE),
                    BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                    this.parentScreen.getBook().getDefaultTitleColor());

            BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                    BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            BookContentRenderer.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                    BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);

            BookPageRenderer.renderBookTextHolder(guiGraphics, this.infoText, this.font,
                    BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22));
        }


        if (!this.searchField.getValue().isEmpty()) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            //draw search field bg
            BookContentRenderer.drawFromContentTexture(guiGraphics, this.parentScreen.getBook(), this.searchField.getX() - 8, this.searchField.getY(), 140, 183, 99, 14);
            var searchComponent = Component.literal(this.searchField.getValue());
            guiGraphics.drawString(this.font, searchComponent, this.searchField.getX() + 7, this.searchField.getY() + 1, 0, false);
        }

        if (this.visibleEntries.isEmpty()) {
            if (!this.searchField.getValue().isEmpty()) {
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS), BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, 80, 0x333333);
                guiGraphics.pose().scale(2F, 2F, 2F);
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS_SAD), BookEntryScreen.RIGHT_PAGE_X / 2 + BookEntryScreen.PAGE_WIDTH / 4, 47, 0x999999);
                guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
            } else {
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.SEARCH_NO_RESULTS), BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, 80, 0x333333);
            }
        }
        guiGraphics.pose().popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }


    @Override
    public void onClose() {
        //Search screen is not supposed to close everything on Esc, so we just pop a layer.
        ClientServices.GUI.popGuiLayer();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        String currQuery = this.searchField.getValue();

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                var entry = this.visibleEntries.get(0);
                BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
                return true;
            }
        } else if (this.searchField.keyPressed(key, scanCode, modifiers)) {
            if (!this.searchField.getValue().equals(currQuery)) {
                this.createEntryList();
            }

            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void init() {
        super.init();

        var textRenderer = new BookTextRenderer(this.getBook(), this.minecraft.level.registryAccess());
        this.prerenderMarkdown(textRenderer);

        //we filter out entries that are locked or in locked categories
        this.allEntries = this.getEntries().stream().filter(e ->
                BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e.getCategory()) &&
                        BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e)
        ).sorted(Comparator.comparing(a -> I18n.get(a.getName()))).toList();

        //TODO: should we NOT filter out locked but visible entries and display them with a lock?

        this.createSearchBar();
        this.createEntryList();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }

        return this.searchField.mouseClicked(pMouseX - this.bookLeft, pMouseY - this.bookTop, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean charTyped(char c, int i) {
        String currQuery = this.searchField.getValue();
        if (this.searchField.charTyped(c, i)) {
            if (!this.searchField.getValue().equals(currQuery)) {
                this.createEntryList();
            }

            return true;
        }

        return super.charTyped(c, i);
    }

    void addEntryButtons(int x, int y, int start, int count) {
        for (int i = 0; i < count && (i + start) < this.visibleEntries.size(); i++) {
            Button button = new EntryListButton(this.visibleEntries.get(start + i), this.bookLeft + x, this.bookTop + y + i * 11, this::handleButtonEntry);
            this.addRenderableWidget(button);
            this.entryButtons.add(button);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
