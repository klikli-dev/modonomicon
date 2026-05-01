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
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryListButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.util.TextRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BookRecentlyUnlockedScreen extends BookPaginatedScreen {
    public static final int ENTRIES_PER_PAGE = 11;
    public static final int ENTRIES_IN_FIRST_PAGE = 9;
    protected final List<Button> entryButtons = new ArrayList<>();
    protected final BookParentScreen parentScreen;
    private final List<BookAddress> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<BookAddress> allEntries;
    private BookTextHolder infoText;
    private List<Component> tooltip;

    public BookRecentlyUnlockedScreen(BookParentScreen parentScreen) {
        super(Component.translatable(Gui.RECENTLY_UNLOCKED_SCREEN_TITLE));
        this.parentScreen = parentScreen;

        this.infoText = new BookTextHolder(Gui.RECENTLY_UNLOCKED_INFO_TEXT);
    }

    public void handleButtonEntry(Button button) {
        if (button instanceof EntryListButton entry) {
            if (!BookUnlockStateManager.get().isUnlockedFor(Minecraft.getInstance().player, entry.getEntry())) {
                return;
            }

            this.onClose();
            if (entry.getAddressToOpen() != null)
                BookGuiManager.get().openBook(entry.getAddressToOpen());
            else
                BookGuiManager.get().openEntry(entry.getEntry().getBook().getId(), entry.getEntry().getId(), 0);
        }
    }

    public void prerenderMarkdown(BookTextRenderer textRenderer) {

        if (!this.infoText.hasComponent()) {
            this.infoText = new RenderedBookTextHolder(this.infoText, textRenderer.render(this.infoText.getString()));
        }
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, Component s, int x, int y, int color) {
        this.drawCenteredStringNoShadow(guiGraphics, s, x, y, color, 1.0f);
    }

    public void drawCenteredStringNoShadow(GuiGraphicsExtractor guiGraphics, Component s, int x, int y, int color, float scale) {
        TextRenderHelper.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
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


    protected void drawTooltip(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.setTooltipForNextFrame(this.tooltip.stream().map(Component::getVisualOrderText).toList(), pMouseX, pMouseY);
        }
    }

    protected void onPageChanged() {
        this.createEntryList();
    }

    protected void resetTooltip() {
        this.tooltip = null;
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

    @Override
    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
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

        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }


    @Override
    public void onClose() {
        //Recently unlocked screen is not supposed to close everything on Esc, so we just pop a layer.
        ClientServices.GUI.popGuiLayer();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                var entry = this.visibleEntries.get(0);
                this.onClose();
                BookGuiManager.get().openBook(entry);
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

        //get recently unlocked entries sorted by timestamp desc, with unread entries prioritized
        Book book = this.getBook();
        Map<Identifier, Long> timestamps = BookUnlockStateManager.get().getUnlockTimestampsFor(this.minecraft.player, book);

        record EntryWithTimestamp(BookEntry entry, long timestamp, boolean unread) {}

        List<EntryWithTimestamp> entriesWithTs = new ArrayList<>();

        for (BookEntry entry : book.getEntries().values()) {
            Long ts = timestamps.get(entry.getId());
            if (ts != null) {
                boolean unread = !BookUnlockStateManager.get().isReadFor(this.minecraft.player, entry);
                entriesWithTs.add(new EntryWithTimestamp(entry, ts, unread));
            }
        }

        // Sort: unread entries first, then by timestamp descending (most recent first)
        entriesWithTs.sort(
                Comparator.comparing(EntryWithTimestamp::unread, Comparator.reverseOrder())
                        .thenComparing(EntryWithTimestamp::timestamp, Comparator.reverseOrder())
        );

        this.allEntries = new ArrayList<>();
        for (EntryWithTimestamp ewt : entriesWithTs) {
            BookAddress address = BookAddress.defaultFor(ewt.entry());
            this.allEntries.add(address);
        }

        this.createEntryList();
    }

    void addEntryButtons(int x, int y, int start, int count) {
        for (int i = 0; i < count && (i + start) < this.visibleEntries.size(); i++) {
            var address = this.visibleEntries.get(start + i);
            Button button = new EntryListButton(this.getBook().getEntry(address.entryId()), address, this.bookLeft + x, this.bookTop + y + i * 13, this::handleButtonEntry);
            this.addRenderableWidget(button);
            this.entryButtons.add(button);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
