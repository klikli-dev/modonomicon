/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookStageRequirementsPage;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.RequirementBlock;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.RequirementContext;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.ResearchRequirementInteractions;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.ResearchRequirementRows;
import com.klikli_dev.modonomicon.client.gui.book.entry.requirement.RowAction;
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
 * Owns the research requirements of a {@link BookEntryScreen}: the requirement blocks of the entry progression and its stages,
 * the browsed stage, the space reserved for the first page block and the trailing requirements page.
 * The screen delegates requirement state, layout, clicks and rendering to this controller.
 * <p>
 * Requirement blocks depend on live state (held items, levels, research), so they are not cached across frames.
 * Instead, each block is built at most once per frame: {@link #beginFrame()} clears the per-frame memo at the start of a frame,
 * and rendering, reserved heights and clicks all share it.
 */
public class EntryRequirementsController {

    /**
     * Id of the client-only trailing requirements page. Not part of the book data, so it is never persisted.
     */
    private static final String TRAILING_PAGE_ID = "__requirements";
    private static final int FIRST_PAGE_INDEX = 0;

    /**
     * The key of a block in the per-frame memo. A null stage is the entry progression, showing the given browsed stage index or the next stage if null.
     */
    private record BlockKey(@Nullable String stage, @Nullable Integer browsed) {
    }

    private final BookEntryScreen screen;

    /**
     * Blocks built during the current frame. Cleared at the start of every frame and whenever the browsed stage changes.
     */
    private final Map<BlockKey, RequirementBlock> frameBlocks = new HashMap<>();

    /**
     * The 0-based stage index of the progression the player browses with the stage line, null while the next stage is shown.
     * Reset when the research state changes and whenever the entry is opened.
     */
    @Nullable
    private Integer browsedStage;

    @Nullable
    private BookStageRequirementsPage trailingPage;

    public EntryRequirementsController(BookEntryScreen screen) {
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
     * True if the requirements of the entry progression are shown at the bottom of the first page.
     */
    public boolean isRequirementsOnFirstPage() {
        return this.screen.getEntry().getProgression() != null
                && this.screen.getEntry().getRequirementsDisplay() == RequirementsDisplay.FIRST_PAGE;
    }

    /**
     * True if the requirements of the entry progression get a virtual page after the last unlocked page.
     */
    public boolean showsTrailingPage() {
        return this.screen.getEntry().getProgression() != null
                && this.screen.getEntry().getRequirementsDisplay() == RequirementsDisplay.TRAILING_PAGE;
    }

    /**
     * The client-only page that shows the requirements of the entry progression. Its page number is after all authored pages and tabs,
     * so it never collides with them.
     */
    public BookStageRequirementsPage getTrailingPage() {
        if (this.trailingPage == null) {
            var entry = this.screen.getEntry();
            var page = new BookStageRequirementsPage(null, null, null, TRAILING_PAGE_ID, new BookNoneCondition(), List.of());
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
        return this.screen.openPagesIndex == FIRST_PAGE_INDEX && this.isRequirementsOnFirstPage() && !this.getBlock(null).isEmpty();
    }

    /**
     * The height at the bottom of the first page that is reserved for the requirements block. Zero if the requirements are not shown there.
     */
    public int getReservedFirstPageHeight() {
        return this.isRequirementsOnFirstPage() ? this.getBlockMaxHeight(null) : 0;
    }

    /**
     * The tallest height the requirement block of a stage can have. For the entry progression, this is the tallest block over all stages,
     * so browsing the stages does not move the text.
     *
     * @param stage the research stage id, or null for the entry progression
     */
    public int getBlockMaxHeight(@Nullable String stage) {
        if (stage != null) {
            return EntryRequirementsRenderer.getBlockHeight(this.getBlock(stage));
        }

        var block = this.getBlock(null);
        int height = EntryRequirementsRenderer.getBlockHeight(block);
        if (block.stage() != null) {
            for (int index = 0; index < block.stage().total(); index++) {
                height = Math.max(height, EntryRequirementsRenderer.getBlockHeight(this.getBuiltBlock(new BlockKey(null, index))));
            }
        }
        return height;
    }

    /**
     * The requirement block of a research stage, or of the entry progression if stage is null. The progression shows the browsed stage, if any.
     * Empty if there is nothing to show.
     *
     * @param stage the research stage id, or null for the entry progression
     */
    public RequirementBlock getBlock(@Nullable String stage) {
        if (stage != null) {
            return this.getBuiltBlock(new BlockKey(stage, null));
        }
        return this.getBuiltBlock(new BlockKey(null, this.browsedStage));
    }

    private RequirementBlock getBuiltBlock(BlockKey key) {
        return this.frameBlocks.computeIfAbsent(key, this::buildBlock);
    }

    private RequirementBlock buildBlock(BlockKey key) {
        var entry = this.screen.getEntry();
        var player = this.screen.getMinecraft().player;
        if (player == null || (key.stage() == null && entry.getProgression() == null)) {
            return RequirementBlock.empty();
        }

        var context = new RequirementContext(
                BookConditionContext.of(entry.getBook(), entry),
                player,
                ResearchServices.state().getStateFor(player),
                ResearchDataManager.get().data()
        );
        if (key.stage() == null) {
            return ResearchRequirementRows.progression(entry.getProgression().node(), key.browsed(), context);
        }
        var stageId = Identifier.tryParse(key.stage());
        return stageId != null ? ResearchRequirementRows.stageBlock(stageId, context) : RequirementBlock.empty();
    }

    /**
     * Moves the browsed stage of the progression by the given delta, limited to the stages that are already reached.
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
        BookGuiManager.get().openEntry(bookId, entryId, 0);
    }

    /**
     * The y coordinate (book-relative) at which a block starts, so that it ends at the bottom of a page starting at pageTop.
     */
    public int getBlockTop(RequirementBlock block, int pageTop) {
        return EntryRequirementsRenderer.getBlockTop(block, pageTop, BookEntryScreen.PAGE_HEIGHT);
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
    public void renderBlock(GuiGraphicsExtractor guiGraphics, RequirementBlock block, int x, int top, int width,
                            int mouseX, int mouseY, boolean interactive) {
        EntryRequirementsRenderer.render(guiGraphics, this.screen.getContentFont(), this.screen.getBook().theme().content(), block,
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
    public boolean clickBlock(RequirementBlock block, int x, int top, int width, double mouseX, double mouseY) {
        return EntryRequirementsRenderer.click(block, this.screen.getContentFont(), x, top, width, mouseX, mouseY, this::browseStage, this::performRowAction);
    }

    private void performRowAction(RowAction action) {
        ResearchRequirementInteractions.perform(action, this::openEntry);
    }
}
