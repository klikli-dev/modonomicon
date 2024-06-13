/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.index;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookCategoryIndexScreen extends BookPaginatedScreen implements BookCategoryScreen {
    public static final int ENTRIES_PER_PAGE = 13;
    public static final int ENTRIES_IN_FIRST_PAGE = 11;
    protected final List<Button> entryButtons = new ArrayList<>();
    protected final BookParentScreen parentScreen;
    protected final BookCategory category;
    private final List<BookEntry> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<BookEntry> allEntries;
    private List<Component> tooltip;

//TODO: Book Category Index screen and Book Parent Index Screen are almost identical and need to be refactored to use a common parent

    public BookCategoryIndexScreen(BookParentScreen parentScreen, BookCategory category){
        this(parentScreen, category, true);
    }

    public BookCategoryIndexScreen(BookParentScreen parentScreen, BookCategory category, boolean addExitButton) {
        super(Component.translatable(category.getName()), addExitButton);
        this.parentScreen = parentScreen;
        this.category = category;
    }

    public void handleButtonEntry(Button button) {
        var entry = ((EntryListButton) button).getEntry();
        BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
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

    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {

            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                BookEntryScreen.playTurnPageSound(this.parentScreen.getBook());
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

    protected boolean shouldShowDescription(){
        return !this.category.getDescription().isEmpty();
    }

    private void createEntryList() {
        this.entryButtons.forEach(b -> {
            this.renderables.remove(b);
            this.children().remove(b);
            this.narratables.remove(b);
        });

        this.entryButtons.clear();
        this.visibleEntries.clear();

        //here we could do some filtering like on the search screen
        this.visibleEntries.addAll(this.allEntries);

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
            if (this.shouldShowDescription()) {
                //only show on the right for the first page
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
            } else {
                this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, ENTRIES_IN_FIRST_PAGE, ENTRIES_PER_PAGE);
            }
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
        return this.category.getEntries().values().stream().toList();
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
        if(BookGuiManager.get().openBookEntryScreen != null) //do not render self while an entry screen is open to avoid double render effects
            return;

        this.resetTooltip();

        //we need to modify blit offset (now: z pose) to not draw over toasts
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -1300);  //magic number arrived by testing until toasts show, but BookOverviewScreen does not
        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);

        BookEntryScreen.renderBookBackground(guiGraphics, this.getBook().getBookContentTexture());


        if (this.openPagesIndex == 0) {
            if (!this.shouldShowDescription()) {
                this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.parentScreen.getBook().getDefaultTitleColor());

                BookEntryScreen.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            } else {
                this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.parentScreen.getBook().getDefaultTitleColor());
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.CATEGORY_INDEX_LIST_TITLE),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.parentScreen.getBook().getDefaultTitleColor());

                BookEntryScreen.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
                BookEntryScreen.drawTitleSeparator(guiGraphics, this.parentScreen.getBook(),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);

                BookPageRenderer.renderBookTextHolder(guiGraphics, this.category.getDescription(), this.font,
                        BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH);
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
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                var entry = this.visibleEntries.get(0);
                BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
                return true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void init() {
        super.init();

        //we filter out entries that are locked or in locked categories
        this.allEntries = this.getEntries().stream().filter(e ->
                        BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e.getCategory()) &&
                                BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e)
                ).sorted(Comparator.comparingInt(BookEntry::getSortNumber)
                        .thenComparing(a -> I18n.get(a.getName())))
                .toList();

        //TODO: should we NOT filter out locked but visible entries and display them with a lock or greyed out?
        // + tooltip?

        this.createEntryList();
    }

    void addEntryButtons(int x, int y, int start, int count) {
        for (int i = 0; i < count && (i + start) < this.visibleEntries.size(); i++) {
            Button button = new EntryListButton(this.visibleEntries.get(start + i), this.bookLeft + x, this.bookTop + y + i * 11, this::handleButtonEntry);
            this.addRenderableWidget(button);
            this.entryButtons.add(button);
        }
    }
}
