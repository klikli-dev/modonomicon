/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class BookSearchScreen extends Screen {
    public static final int ENTRIES_PER_PAGE = 13;
    public static final int ENTRIES_IN_FIRST_PAGE = 11;

    private final BookOverviewScreen parentScreen;
    private final List<BookEntry> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<BookEntry> allEntries;
    private EditBox searchField;
    protected final List<Button> entryButtons = new ArrayList<>();

    private BookTextHolder infoText;

    protected BookSearchScreen(BookOverviewScreen parentScreen) {
        super(Component.literal(""));

        this.parentScreen = parentScreen;
        this.infoText = new BookTextHolder(Gui.SEARCH_INFO_TEXT);
    }

    private void createSearchBar() {
        this.searchField = new EditBox(font, 160, 170, 90, 12, Component.literal(""));
        this.searchField.setMaxLength(32);
        this.searchField.setCanLoseFocus(false);
        this.searchField.changeFocus(true);
    }

    private void createEntryList() {
        //TODO: Implement


        this.entryButtons.forEach(b -> {
            this.renderables.remove(b);
            this.children().remove(b);
            this.narratables.remove(b);
        });

        entryButtons.clear();
        visibleEntries.clear();

        String query = searchField.getValue().toLowerCase();
        allEntries.stream().filter((e) -> e.matchesQuery(query)).forEach(visibleEntries::add);

        maxOpenPagesIndex = 1;
        int count = visibleEntries.size();
        count -= ENTRIES_IN_FIRST_PAGE;
        if (count > 0) {
            maxOpenPagesIndex += (int) Math.ceil((float) count / (ENTRIES_PER_PAGE * 2));
        }

        while (getEntryCountStart() > visibleEntries.size()) {
            openPagesIndex--;
        }

        if (openPagesIndex == 0) {
            addEntryButtons(BookContentScreen.RIGHT_PAGE_X, BookContentScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
            addSubcategoryButtons();
        } else {
            int start = getEntryCountStart();
            addEntryButtons(BookContentScreen.LEFT_PAGE_X, BookContentScreen.TOP_PADDING, start, ENTRIES_PER_PAGE);
            addEntryButtons(BookContentScreen.RIGHT_PAGE_X, BookContentScreen.TOP_PADDING, start + ENTRIES_PER_PAGE, ENTRIES_PER_PAGE);
        }
    }

    private List<BookEntry> getEntries() {
        return this.parentScreen.getBook().getEntries().values().stream().toList();
    }

    @Override
    public void init() {
        super.init();

        var textRenderer = new BookTextRenderer();
        this.prerenderMarkdown(textRenderer);

        //we filter out entries that are locked or in locked categories
        allEntries = getEntries().stream().filter(e ->
                BookUnlockCapability.isUnlockedFor(this.minecraft.player, e.getCategory()) &&
                        BookUnlockCapability.isUnlockedFor(this.minecraft.player, e)
        ).sorted().toList();

        //TODO: should we NOT filter out locked but visible entries and display them with a lock?

        createSearchBar();
        createEntryList();
    }

    public void prerenderMarkdown(BookTextRenderer textRenderer) {

        if (!this.infoText.hasComponent()) {
            this.infoText = new RenderedBookTextHolder(this.infoText, textRenderer.render(this.infoText.getString()));
        }
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        if (openPagesIndex == 0) {
            drawCenteredStringNoShadow(pPoseStack, getTitle(),
                    BookContentScreen.LEFT_PAGE_X + BookContentScreen.PAGE_WIDTH / 2, BookContentScreen.TOP_PADDING,
                    this.parentScreen.getBook().getDefaultTitleColor());
            drawCenteredStringNoShadow(pPoseStack, Component.translatable(Gui.SEARCH_ENTRY_LIST_TITLE),
                    BookContentScreen.RIGHT_PAGE_X + BookContentScreen.PAGE_WIDTH / 2, BookContentScreen.TOP_PADDING,
                    this.parentScreen.getBook().getDefaultTitleColor());

            BookContentScreen.drawTitleSeparator(pPoseStack, this.parentScreen.getBook(), BookContentScreen.LEFT_PAGE_X, BookContentScreen.TOP_PADDING + 12);
            BookContentScreen.drawTitleSeparator(pPoseStack, this.parentScreen.getBook(), BookContentScreen.RIGHT_PAGE_X, BookContentScreen.TOP_PADDING + 12);

            BookPageRenderer.renderBookTextHolder(infoText, this.font, pPoseStack, pMouseX, pMouseY, BookContentScreen.PAGE_WIDTH);
        }

        if (!searchField.getValue().isEmpty()) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            BookContentScreen.drawFromTexture(pPoseStack, this.parentScreen.getBook(), searchField.x - 8, searchField.y, 140, 183, 99, 14);
            var searchComponent = Component.literal(searchField.getValue());
            //TODO: if we want to support a font style, we set it here
            font.draw(pPoseStack, searchComponent, searchField.x + 7, searchField.y + 1, 0);
        }

        if (visibleEntries.isEmpty()) {
            if (!searchField.getValue().isEmpty()) {
                drawCenteredStringNoShadow(pPoseStack, Component.translatable(Gui.SEARCH_NO_RESULTS), BookContentScreen.RIGHT_PAGE_X + BookContentScreen.PAGE_WIDTH / 2, 80, 0x333333);
                pPoseStack.scale(2F, 2F, 2F);
                drawCenteredStringNoShadow(pPoseStack, Component.translatable(Gui.SEARCH_NO_RESULTS_SAD), BookContentScreen.RIGHT_PAGE_X / 2 + BookContentScreen.PAGE_WIDTH / 4, 47, 0x999999);
                pPoseStack.scale(0.5F, 0.5F, 0.5F);
            } else {
                drawCenteredStringNoShadow(pPoseStack, Component.translatable(Gui.SEARCH_NO_RESULTS), BookContentScreen.RIGHT_PAGE_X + BookContentScreen.PAGE_WIDTH / 2, 80, 0x333333);
            }
        }

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void drawCenteredStringNoShadow(PoseStack poseStack, Component s, int x, int y, int color) {
        this.drawCenteredStringNoShadow(poseStack, s, x, y, color, 1.0f);
    }

    public void drawCenteredStringNoShadow(PoseStack poseStack, Component s, int x, int y, int color, float scale) {
        this.font.draw(poseStack, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color);
    }
}
