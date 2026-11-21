/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.ScholarshipDisplay;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookScholarshipPage;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipBlock;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipContext;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipInteractions;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipRows;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipRowAction;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the research requirements of a {@link BookEntryScreen}: the requirement blocks of the entry research and its stages,
 * the browsed stage, the space reserved for the first page block and the trailing scholarship page.
 * The screen delegates requirement state, layout, clicks and rendering to this controller.
 * <p>
 * Requirement blocks depend on live state (held items, levels, research), so they are not cached across frames.
 * Instead, each block is built at most once per frame: {@link #beginFrame()} clears the per-frame memo at the start of a frame,
 * and rendering, reserved heights and clicks all share it.
 */
public class EntryScholarshipController {

    /**
     * Id of the client-only trailing scholarship page. Not part of the book data, so it is never persisted.
     */
    private static final String TRAILING_PAGE_ID = "__scholarship";
    private static final int FIRST_PAGE_INDEX = 0;
    private static final int ENTRY_START_PAGE = 0;

    /**
     * The key of a block in the per-frame memo. A null stage is the entry research, showing the given browsed stage index or the next stage if null.
     */
    private record BlockKey(@Nullable String stage, @Nullable Integer browsed) {
    }

    private final BookEntryScreen screen;

    /**
     * Owns the completion requests in flight of this screen, shared by the renderer's complete button.
     */
    private final ScholarshipInteractions interactions = new ScholarshipInteractions();
    private final EntryScholarshipRenderer renderer = new EntryScholarshipRenderer(this.interactions);

    /**
     * Blocks built during the current frame. Cleared at the start of every frame and whenever the browsed stage changes.
     */
    private final Map<BlockKey, ScholarshipBlock> frameBlocks = new HashMap<>();

    /**
     * The 0-based stage index of the research the player browses with the stage line, null while the next stage is shown.
     * Reset when the research state changes and whenever the entry is opened.
     */
    @Nullable
    private Integer browsedStage;

    @Nullable
    private BookScholarshipPage trailingPage;

    public EntryScholarshipController(BookEntryScreen screen) {
        this.screen = screen;
    }

    /**
     * Must be called at the start of every frame that renders the entry, before any block is requested.
     */
    public void beginFrame() {
        this.frameBlocks.clear();
    }

    /**
     * Must be called when the research state changes while the entry is open, e.g. by a sync message.
     */
    public void onResearchStateChanged() {
        this.frameBlocks.clear();
        this.browsedStage = null;
    }

    /**
     * True if the requirements of the entry research are shown at the bottom of the first page.
     */
    public boolean isScholarshipOnFirstPage() {
        return this.screen.getEntry().getResearch() != null
                && this.screen.getEntry().getScholarshipDisplay() == ScholarshipDisplay.FIRST_PAGE;
    }

    /**
     * True if the requirements of the entry research get a virtual page after the last unlocked page.
     */
    public boolean showsTrailingPage() {
        return this.screen.getEntry().getResearch() != null
                && this.screen.getEntry().getScholarshipDisplay() == ScholarshipDisplay.TRAILING_PAGE;
    }

    /**
     * The client-only page that shows the requirements of the entry research. Its page number is after all authored pages and tabs,
     * so it never collides with them.
     */
    public BookScholarshipPage getTrailingPage() {
        if (this.trailingPage == null) {
            var entry = this.screen.getEntry();
            var page = new BookScholarshipPage(null, null, null, TRAILING_PAGE_ID, new BookNoneCondition(), List.of());
            page.attach(entry, entry.getPages().size() + entry.getTabs().size());
            this.trailingPage = page;
        }
        return this.trailingPage;
    }

    public boolean isTrailingPage(BookPage page) {
        return this.trailingPage != null && page == this.trailingPage;
    }

    /**
     * True if the requirements block is drawn on the first page, i.e. the first page is open on the left.
     */
    public boolean isVisibleOnFirstPage() {
        return this.screen.openPagesIndex == FIRST_PAGE_INDEX && this.isScholarshipOnFirstPage() && !this.getBlock(null).isEmpty();
    }

    /**
     * The height at the bottom of the first page that is reserved for the requirements block. Zero if the requirements are not shown there.
     */
    public int getReservedFirstPageHeight() {
        return this.isScholarshipOnFirstPage() ? this.getBlockMaxHeight(null) : 0;
    }

    /**
     * The tallest height the requirement block of a stage can have. For the entry research, this is the tallest block over all stages,
     * so browsing the stages does not move the text.
     *
     * @param stage the research stage id, or null for the entry research
     */
    public int getBlockMaxHeight(@Nullable String stage) {
        if (stage != null) {
            return EntryScholarshipRenderer.getBlockHeight(this.getBlock(stage));
        }

        var block = this.getBlock(null);
        int height = EntryScholarshipRenderer.getBlockHeight(block);
        if (block.stage() != null) {
            for (int index = 0; index < block.stage().total(); index++) {
                height = Math.max(height, EntryScholarshipRenderer.getBlockHeight(this.getBuiltBlock(new BlockKey(null, index))));
            }
        }
        return height;
    }

    /**
     * The requirement block of a research stage, or of the entry research if stage is null. The research shows the browsed stage, if any.
     * Empty if there is nothing to show.
     *
     * @param stage the research stage id, or null for the entry research
     */
    public ScholarshipBlock getBlock(@Nullable String stage) {
        if (stage != null) {
            return this.getBuiltBlock(new BlockKey(stage, null));
        }
        return this.getBuiltBlock(new BlockKey(null, this.browsedStage));
    }

    private ScholarshipBlock getBuiltBlock(BlockKey key) {
        return this.frameBlocks.computeIfAbsent(key, this::buildBlock);
    }

    private ScholarshipBlock buildBlock(BlockKey key) {
        var entry = this.screen.getEntry();
        var player = this.screen.getMinecraft().player;
        if (player == null || (key.stage() == null && entry.getResearch() == null)) {
            return ScholarshipBlock.empty();
        }

        var context = new ScholarshipContext(
                BookConditionContext.of(entry.getBook(), entry),
                player,
                ResearchServices.state().getStateFor(player),
                ResearchDataManager.get().data()
        );
        if (key.stage() == null) {
            return ScholarshipRows.research(entry.getResearch().node(), key.browsed(), context);
        }
        var stageId = Identifier.tryParse(key.stage());
        return stageId != null ? ScholarshipRows.stageBlock(stageId, context) : ScholarshipBlock.empty();
    }

    /**
     * Moves the browsed stage of the research by the given delta, limited to the stages that are already reached.
     * Browsing to the next stage returns to the default view.
     *
     * @return true if the click was consumed by the stage line
     */
    public boolean browseStage(int delta) {
        var stage = this.getBlock(null).stage();
        if (stage == null || !stage.navigable() || delta == 0) {
            return false;
        }
        int target = Math.max(0, Math.min(stage.index() + delta, stage.lastBrowsable()));
        if (target == stage.index()) {
            return true;
        }
        this.browsedStage = target == stage.lastBrowsable() ? null : target;
        this.frameBlocks.clear();
        return true;
    }

    private void openEntry(Identifier bookId, Identifier entryId) {
        //same navigation as entry links, so back navigation returns to this entry
        this.screen.pushCurrentToHistory();
        BookGuiManager.get().openEntry(bookId, entryId, ENTRY_START_PAGE);
    }

    /**
     * The y coordinate (book-relative) at which a block starts, so that it ends at the bottom of a page starting at pageTop.
     */
    public int getBlockTop(ScholarshipBlock block, int pageTop) {
        return EntryScholarshipRenderer.getBlockTop(block, pageTop, BookEntryScreen.PAGE_HEIGHT);
    }

    /**
     * Renders the requirements block at the bottom of the first page. Call with the book origin as translation.
     *
     * @param mouseX      screen mouse x
     * @param mouseY      screen mouse y
     * @param interactive whether hovering shows tooltips
     */
    public void renderFirstPage(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean interactive) {
        if (!this.isVisibleOnFirstPage()) {
            return;
        }

        var block = this.getBlock(null);
        this.renderBlock(guiGraphics, block, BookEntryScreen.LEFT_PAGE_X, this.getBlockTop(block, BookEntryScreen.TOP_PADDING),
                BookEntryScreen.PAGE_WIDTH, mouseX - this.screen.getBookLeft(), mouseY - this.screen.getBookTop(), interactive);
    }

    /**
     * Renders a block at the given position. Call with the book origin as translation, i.e. coordinates are book-relative.
     */
    public void renderBlock(GuiGraphicsExtractor guiGraphics, ScholarshipBlock block, int x, int top, int width,
                            int mouseX, int mouseY, boolean interactive) {
        this.renderer.render(guiGraphics, this.screen.getContentFont(), this.screen.getBook().theme().content(), block,
                x, top, width, mouseX, mouseY, interactive, this.screen::setTooltip);
    }

    /**
     * Handles a click on the first page requirements block. Clicks never reach the block while the tab overlay is open.
     *
     * @return true if the click was consumed
     */
    public boolean handleMouseClicked(MouseButtonEvent event) {
        if (this.screen.entryTabs.isOpen() || !this.isVisibleOnFirstPage()) {
            return false;
        }

        var block = this.getBlock(null);
        return this.clickBlock(block, BookEntryScreen.LEFT_PAGE_X, this.getBlockTop(block, BookEntryScreen.TOP_PADDING),
                BookEntryScreen.PAGE_WIDTH, event.x() - this.screen.getBookLeft(), event.y() - this.screen.getBookTop());
    }

    /**
     * Handles a click on a block at the given position. Coordinates are relative to the same origin as the layout.
     *
     * @return true if the click was consumed
     */
    public boolean clickBlock(ScholarshipBlock block, int x, int top, int width, double mouseX, double mouseY) {
        return this.renderer.click(block, this.screen.getContentFont(), x, top, width, mouseX, mouseY, this::browseStage, this::performScholarshipRowAction);
    }

    private void performScholarshipRowAction(ScholarshipRowAction action) {
        ScholarshipInteractions.perform(action, this::openEntry);
    }
}
