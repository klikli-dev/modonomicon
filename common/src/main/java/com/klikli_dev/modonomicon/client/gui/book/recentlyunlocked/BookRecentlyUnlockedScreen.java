/*
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.recentlyunlocked;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedListScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BookRecentlyUnlockedScreen extends BookPaginatedListScreen<BookAddress> {
    protected final BookParentScreen parentScreen;
    private BookTextHolder infoText;

    public BookRecentlyUnlockedScreen(BookParentScreen parentScreen) {
        super(Component.translatable(Gui.RECENTLY_UNLOCKED_SCREEN_TITLE));
        this.parentScreen = parentScreen;

        this.infoText = new BookTextHolder(Gui.RECENTLY_UNLOCKED_INFO_TEXT);
    }

    @Override
    public void handleButtonEntry(Button button) {
        if (button instanceof EntryListButton entry) {
            if (!BookServices.visibility().isAccessible(Minecraft.getInstance().player, entry.getEntry())) {
                return;
            }

            this.onClose();
            if (entry.getAddressToOpen() != null)
                BookGuiManager.get().openBook(entry.getAddressToOpen());
            else
                BookGuiManager.get().openEntry(entry.getEntry().getBook().getId(), entry.getEntry().getId(), 0);
        }
    }

    @Override
    protected void openOnlyVisibleEntry() {
        var entry = this.visibleEntries.get(0);
        this.onClose();
        BookGuiManager.get().openBook(entry);
    }

    @Override
    protected boolean fillLeftPageOnFirstPage() {
        //the info text takes the left page, so the list starts on the right
        return false;
    }

    @Override
    protected List<BookAddress> computeAllEntries() {
        //get recently unlocked entries sorted by timestamp desc, with unread entries prioritized
        Book book = this.getBook();
        BookVisibilitySnapshots.collect(this.minecraft.player, book);
        Map<Identifier, Long> timestamps = BookServices.stateAccess().getUnlockTimestamps(this.minecraft.player, book);

        record EntryWithTimestamp(BookEntry entry, long timestamp, boolean unread) {}

        List<EntryWithTimestamp> entriesWithTs = new ArrayList<>();

        for (BookEntry entry : book.getEntries().values()) {
            Long ts = timestamps.get(entry.getId());
            if (ts != null) {
                boolean unread = BookServices.stateAccess().isEntryUnread(this.minecraft.player, entry);
                entriesWithTs.add(new EntryWithTimestamp(entry, ts, unread));
            }
        }

        // Sort: unread entries first, then by timestamp descending (most recent first)
        entriesWithTs.sort(
                Comparator.comparing(EntryWithTimestamp::unread, Comparator.reverseOrder())
                        .thenComparing(EntryWithTimestamp::timestamp, Comparator.reverseOrder())
        );

        List<BookAddress> allEntries = new ArrayList<>();
        for (EntryWithTimestamp ewt : entriesWithTs) {
            BookAddress address = BookAddress.defaultFor(ewt.entry());
            allEntries.add(address);
        }
        return allEntries;
    }

    @Override
    protected Button createEntryButton(BookAddress address, int x, int y) {
        return new EntryListButton(this.getBook().getEntry(address.entryId()), address, x, y, this::handleButtonEntry);
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
            this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.RECENTLY_UNLOCKED_ENTRY_LIST_TITLE),
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

        if (this.visibleEntries.isEmpty()) {
            this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.RECENTLY_UNLOCKED_NO_RESULTS), BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, 80, 0x333333);
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
        //Recently unlocked screen is not supposed to close everything on Esc, so we just pop a layer.
        ClientServices.GUI.popGuiLayer();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation()) {
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

        var textRenderer = new BookTextRenderer(this.getBook(), this.minecraft.level.registryAccess());
        this.prerenderMarkdown(textRenderer);

        this.allEntries = this.computeAllEntries();

        this.createEntryList();
    }
}
