/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipBlock;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipRow;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipInteractions;
import com.klikli_dev.modonomicon.client.gui.book.entry.scholarship.ScholarshipRowAction;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookContentTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.research.data.ResearchGroupDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * Renders a requirement block, e.g. the requirements of the next stage of an entry research.
 * <p>
 * Below the separator, every requirement category is one row (ordered by category sort order): a faint category icon
 * at the left edge, a faint line fading out to the right edge of the page, and the requirement icons on top of the line.
 * Icons are spaced by {@link #SLOT_STEP} and compressed when crowded. A category with more icons than fit into one row
 * wraps into additional rows of the same category.
 * <p>
 * A staged block ends with a stage line ("Stage k/n") with small arrows to browse the stages, see {@link #stageLineClick}.
 * Coordinates are relative to the book (bookLeft/bookTop), the block is aligned to the bottom of the page.
 */
public final class EntryScholarshipRenderer {

    public static final int ROW_HEIGHT = 18;
    public static final int SLOT_SIZE = 16;
    public static final int SLOT_STEP = 18;
    /**
     * The smallest icon spacing before icons wrap into an additional row.
     */
    public static final int MIN_SLOT_STEP = 12;
    public static final int ROW_PADDING = (ROW_HEIGHT - SLOT_SIZE) / 2;
    public static final int CATEGORY_ICON_SIZE = 16;
    public static final int CATEGORY_LINE_HEIGHT = 3;
    /**
     * The x offset of the first requirement icon of a row, relative to the left edge of the block.
     */
    public static final int ICONS_OFFSET = 20;
    public static final int SEPARATOR_WIDTH = 112;
    public static final int SEPARATOR_HEIGHT = 7;
    public static final int SEPARATOR_GAP = 3;
    public static final int CHECK_WIDTH = 10;
    public static final int CHECK_HEIGHT = 7;
    public static final int STAGE_LINE_HEIGHT = 10;
    public static final int BOTTOM_MARGIN = 2;
    public static final int STAGE_ARROW_WIDTH = 5;
    public static final int STAGE_ARROW_HEIGHT = 7;
    private static final int STAGE_ARROW_GAP = 4;

    private static final int CATEGORY_ICON_TINT = 0x55FFFFFF;
    private static final int ACTION_HIGHLIGHT_COLOR = 0x332D2716;
    private static final int CATEGORY_LINE_TINT = 0xFFFFFFFF;
    private static final int COUNT_COLOR = 0xFFFFFFFF;
    private static final int COUNT_Y_OFFSET = 9;
    private static final int COUNT_RIGHT_OFFSET = 17;
    private static final int STAGE_TEXT_COLOR = 0xFF2D2716;
    private static final int COMPLETE_DISABLED_COLOR = 0xFF8A8577;
    private static final int BUTTON_PADDING = 3;
    /** Gap below the complete button band, before the first requirement row. */
    private static final int BUTTON_BAND_GAP = 2;
    private static final int MIN_ICONS_PER_ROW = 1;
    private static final int SLOT_HIGHLIGHT_OUTSET = 1;
    private static final int TEXT_INSET_Y = 1;
    private static final int CATEGORY_LINE_GAP = 1;
    private static final int STAGE_DELTA_PREVIOUS = -1;
    private static final int STAGE_DELTA_NEXT = 1;

    /**
     * A laid out requirement slot, in book-relative coordinates.
     */
    public record Slot(ScholarshipRow row, int x, int y, int width, int height) {
        public boolean contains(double mouseX, double mouseY) {
            return inside(this.x, this.y, this.width, this.height, mouseX, mouseY);
        }
    }

    /**
     * A row of requirement icons of one category. Only the first row of a category shows the category icon.
     */
    private record Line(ResearchGroupDefinition category, boolean first, int y, List<ScholarshipRow> rows) {
    }

    /**
     * Book-relative geometry of the stage line.
     */
    private record StageGeometry(int textX, int textY, int textWidth, int leftX, int rightX, int arrowY, int stageY, int buttonX, int buttonY, int buttonWidth) {
        int textRight() {
            return this.textX + this.textWidth;
        }
    }

    private final ScholarshipInteractions interactions;

    public EntryScholarshipRenderer(ScholarshipInteractions interactions) {
        this.interactions = interactions;
    }

    /**
     * The requirements grouped by category, categories in their sort order.
     */
    public static Map<ResearchGroupDefinition, List<ScholarshipRow>> group(List<ScholarshipRow> rows) {
        var groups = new LinkedHashMap<ResearchGroupDefinition, List<ScholarshipRow>>();
        rows.stream()
                .sorted(Comparator.comparingInt((ScholarshipRow row) -> row.group().sortOrder())
                        .thenComparing(row -> row.group().id().toString()))
                .forEach(row -> groups.computeIfAbsent(row.group(), k -> new ArrayList<>()).add(row));
        return groups;
    }

    /**
     * The horizontal distance between the icons of a row. Icons are spaced by {@link #SLOT_STEP}, compressed evenly
     * if they would not fit into the width. Always at least one pixel.
     */
    public static int iconStep(int count, int width) {
        if (count <= 1) {
            return SLOT_STEP;
        }
        return Math.max(1, Math.min(SLOT_STEP, (width - SLOT_SIZE) / (count - 1)));
    }

    /**
     * The number of icons that fit into one row of the given width with at least {@link #MIN_SLOT_STEP} spacing.
     */
    public static int maxIconsPerRow(int width) {
        return Math.max(MIN_ICONS_PER_ROW, 1 + (width - SLOT_SIZE) / MIN_SLOT_STEP);
    }

    /**
     * The width available for the icons of a row, right of the category icon.
     */
    private static int iconsWidth(int width) {
        return Math.max(SLOT_SIZE, width - ICONS_OFFSET);
    }

    /**
     * The number of rows needed to show the given number of icons of one category.
     */
    public static int rowCount(int count, int width) {
        if (count <= 0) {
            return 0;
        }
        int perRow = maxIconsPerRow(iconsWidth(width));
        return (count + perRow - 1) / perRow;
    }

    /**
     * The height of the block, independent of rendering. Zero if the block is empty.
     * <p>
     * {@code separator (7) + gap (3) + complete button band (12, manual stages only) + content + stage line (10, if staged) + bottom margin (2)}, where the content is
     * the check mark (7) for a complete block without rows, else {@code 18} per requirement row.
     * Blocks are always laid out with the page width.
     */
    public static int getBlockHeight(ScholarshipBlock block) {
        if (block.isEmpty()) {
            return 0;
        }
        int contentHeight = contentHeight(block, BookEntryScreen.PAGE_WIDTH);
        int stageHeight = block.stage() != null ? STAGE_LINE_HEIGHT : 0;
        return SEPARATOR_HEIGHT + SEPARATOR_GAP + buttonBandHeight(block) + contentHeight + stageHeight + BOTTOM_MARGIN;
    }

    /**
     * The y coordinate (book-relative) at which the block starts, so that it ends at the bottom of the page.
     */
    public static int getBlockTop(ScholarshipBlock block, int pageTop, int pageHeight) {
        return pageTop + pageHeight - getBlockHeight(block);
    }

    /**
     * Lays out the requirement slots of the block at the given position.
     *
     * @param x     left edge of the block (book-relative)
     * @param top   top edge of the block (book-relative)
     * @param width width of the block
     */
    public static List<Slot> layout(ScholarshipBlock block, int x, int top, int width) {
        var slots = new ArrayList<Slot>();
        for (var line : lines(block, x, top, width)) {
            int step = iconStep(line.rows().size(), iconsWidth(width));
            int startX = x + ICONS_OFFSET;
            for (int i = 0; i < line.rows().size(); i++) {
                slots.add(new Slot(line.rows().get(i), startX + i * step, line.y() + ROW_PADDING, SLOT_SIZE, SLOT_SIZE));
            }
        }
        return slots;
    }

    /**
     * Renders the block. Must be called with the book origin as translation, i.e. coordinates are book-relative.
     *
     * @param mouseX      book-relative mouse x (or far off-screen to suppress hover)
     * @param mouseY      book-relative mouse y
     * @param interactive whether hovering shows tooltips
     */
    public void render(GuiGraphicsExtractor guiGraphics, Font font, BookContentTheme content, ScholarshipBlock block,
                              int x, int top, int width, int mouseX, int mouseY, boolean interactive,
                              Consumer<List<Component>> tooltipSink) {
        if (block.isEmpty()) {
            return;
        }

        int separatorX = x + (width - SEPARATOR_WIDTH) / 2;
        content.scholarshipSeparator().extractRenderState(guiGraphics, separatorX, top);
        if (interactive && inside(separatorX, top, SEPARATOR_WIDTH, SEPARATOR_HEIGHT, mouseX, mouseY)) {
            tooltipSink.accept(separatorTooltip(block));
        }

        int contentY = contentY(block, top);
        if (isCheckOnly(block)) {
            int checkX = x + (width - CHECK_WIDTH) / 2;
            content.scholarshipCheck().extractRenderState(guiGraphics, checkX, contentY);
            if (interactive && inside(checkX, contentY, CHECK_WIDTH, CHECK_HEIGHT, mouseX, mouseY)) {
                tooltipSink.accept(List.of(Component.translatable(Gui.SCHOLARSHIP_COMPLETE)));
            }
        } else {
            renderRows(guiGraphics, font, content, block, x, top, width, mouseX, mouseY, interactive, tooltipSink);
        }

        if (block.stage() != null) {
            renderStageLine(guiGraphics, font, content, block, x, top, width, mouseX, mouseY, interactive, tooltipSink);
        }
    }

    /**
     * Handles a click on the block, coordinates relative to the same origin as the layout.
     * Stage arrows are passed to browse. A shift-clicked requirement fact is granted to players that may grant facts.
     * Otherwise a requirement icon performs its action if it has one.
     *
     * @param shiftDown whether shift is held, shift-clicking a requirement grants its fact if the player may grant facts
     * @param browse browses the stages by the given delta, returns true if the click was consumed
     * @param onScholarshipRowAction receives the action of a clicked row, see {@link ScholarshipRowAction}
     * @return true if the click was consumed
     */
    public boolean click(ScholarshipBlock block, Font font, int x, int top, int width, double mouseX, double mouseY, boolean shiftDown,
                                IntPredicate browse, Consumer<ScholarshipRowAction> onScholarshipRowAction) {
        int stageDelta = stageLineClick(block, font, x, top, width, mouseX, mouseY);
        if (stageDelta != 0) {
            return browse.test(stageDelta);
        }
        if (clickCompleteButton(block, font, x, top, width, mouseX, mouseY)) {
            return true;
        }
        for (var slot : layout(block, x, top, width)) {
            if (slot.contains(mouseX, mouseY)) {
                var row = slot.row();
                if (shiftDown && row.factId() != null && !row.met() && ScholarshipInteractions.canGrantFacts(ScholarshipInteractions.player())) {
                    //the server validates the permission and the fact again
                    ScholarshipInteractions.grantFact(row.factId());
                    return true;
                }
                if (row.action() != null) {
                    onScholarshipRowAction.accept(row.action());
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Returns -1 if the left stage arrow was clicked, 1 for the right arrow, 0 otherwise.
     * Coordinates are relative to the same origin as the layout.
     */
    public static int stageLineClick(ScholarshipBlock block, Font font, int x, int top, int width, double mouseX, double mouseY) {
        if (block.stage() == null) {
            return 0;
        }
        var geometry = stageGeometry(block, font, x, top, width);
        var stage = block.stage();
        if (stage.navigable() && stage.index() > 0 && inside(geometry.leftX(), geometry.arrowY(), STAGE_ARROW_WIDTH, STAGE_ARROW_HEIGHT, mouseX, mouseY)) {
            return STAGE_DELTA_PREVIOUS;
        }
        if (stage.navigable() && stage.index() < stage.lastBrowsable() && inside(geometry.rightX(), geometry.arrowY(), STAGE_ARROW_WIDTH, STAGE_ARROW_HEIGHT, mouseX, mouseY)) {
            return STAGE_DELTA_NEXT;
        }
        return 0;
    }

    /**
     * The y coordinate of the first requirement row: below the separator and, for manual stages, below the complete button band.
     */
    private static int contentY(ScholarshipBlock block, int top) {
        return top + SEPARATOR_HEIGHT + SEPARATOR_GAP + buttonBandHeight(block);
    }

    /**
     * Manual stages reserve a band between the separator and the first row for the complete button, so the button stays
     * on the page regardless of the page side and nothing moves when it appears.
     */
    private static int buttonBandHeight(ScholarshipBlock block) {
        return reservesCompleteButton(block) ? STAGE_LINE_HEIGHT + BUTTON_BAND_GAP : 0;
    }

    private static StageGeometry stageGeometry(ScholarshipBlock block, Font font, int x, int top, int width) {
        int stageY = contentY(block, top) + contentHeight(block, width);
        int textWidth = font.width(stageLabel(block));
        //the label is centered on its own, the arrow slots are symmetric around it, so they never depend on the button
        int textX = x + (width - textWidth) / 2;
        int leftX = textX - STAGE_ARROW_GAP - STAGE_ARROW_WIDTH;
        int rightX = textX + textWidth + STAGE_ARROW_GAP;
        int buttonWidth = reservesCompleteButton(block) ? completeButtonWidth(font) : 0;
        //the button is centered in its own band between the separator and the first requirement row
        int buttonX = x + (width - buttonWidth) / 2;
        int buttonY = top + SEPARATOR_HEIGHT + SEPARATOR_GAP;
        int arrowY = stageY + (STAGE_LINE_HEIGHT - STAGE_ARROW_HEIGHT) / 2;
        return new StageGeometry(textX, stageY + TEXT_INSET_Y, textWidth, leftX, rightX, arrowY, stageY, buttonX, buttonY, buttonWidth);
    }

    /**
     * A manual stage reserves the button space, the button itself is only drawn once its requirements are met.
     */
    private static boolean reservesCompleteButton(ScholarshipBlock block) {
        return block.stage() != null && block.stage().manual() != null;
    }

    /**
     * The complete button is drawn once the requirements of the manual stage are met. It is only ever on the current stage,
     * so it never shares the line with the right browse arrow.
     */
    private static boolean showsCompleteButton(ScholarshipBlock block) {
        return reservesCompleteButton(block) && block.stage().manual().ready();
    }

    private static int completeButtonWidth(Font font) {
        return Math.max(font.width(Component.translatable(Gui.SCHOLARSHIP_STAGE_COMPLETE)),
                font.width(Component.translatable(Gui.SCHOLARSHIP_STAGE_COMPLETING))) + 2 * BUTTON_PADDING;
    }

    private static boolean inCompleteButton(StageGeometry geometry, double mouseX, double mouseY) {
        return geometry.buttonWidth() > 0 && inside(geometry.buttonX(), geometry.buttonY(), geometry.buttonWidth(), STAGE_LINE_HEIGHT, mouseX, mouseY);
    }

    /**
     * Completes the shown manual stage if the complete button was clicked. Coordinates relative to the same origin as the layout.
     */
    private boolean clickCompleteButton(ScholarshipBlock block, Font font, int x, int top, int width, double mouseX, double mouseY) {
        if (!showsCompleteButton(block) || !inCompleteButton(stageGeometry(block, font, x, top, width), mouseX, mouseY)) {
            return false;
        }
        this.interactions.completeStage(block.stage().manual());
        return true;
    }

    private static int contentHeight(ScholarshipBlock block, int width) {
        return isCheckOnly(block) ? CHECK_HEIGHT : rows(block, width) * ROW_HEIGHT;
    }

    /**
     * A complete block without rows (e.g. a research without stages) only shows a check mark.
     * Complete blocks with rows show them, all met.
     */
    private static boolean isCheckOnly(ScholarshipBlock block) {
        return block.complete() && block.rows().isEmpty();
    }

    /**
     * "Stage k/n", followed by a check mark once the whole research is complete.
     */
    private static Component stageLabel(ScholarshipBlock block) {
        var stage = block.stage();
        var label = Component.translatable(Gui.SCHOLARSHIP_STAGE_LINE, stage.index() + 1, stage.total());
        if (block.complete()) {
            return Component.empty().append(label).append(Component.literal(" ✔").withStyle(ChatFormatting.DARK_GREEN));
        }
        return label;
    }

    private void renderStageLine(GuiGraphicsExtractor guiGraphics, Font font, BookContentTheme content, ScholarshipBlock block,
                                        int x, int top, int width, int mouseX, int mouseY, boolean interactive,
                                        Consumer<List<Component>> tooltipSink) {
        var stage = block.stage();
        var geometry = stageGeometry(block, font, x, top, width);
        var label = stageLabel(block);
        guiGraphics.text(font, label, geometry.textX(), geometry.textY(), STAGE_TEXT_COLOR, false);

        if (stage.navigable()) {
            if (stage.index() > 0) {
                boolean hovered = inside(geometry.leftX(), geometry.arrowY(), STAGE_ARROW_WIDTH, STAGE_ARROW_HEIGHT, mouseX, mouseY);
                content.smallPreviousPageButton().state(hovered, false)
                        .extractRenderState(guiGraphics, geometry.leftX(), geometry.arrowY());
            }
            if (stage.index() < stage.lastBrowsable()) {
                boolean hovered = inside(geometry.rightX(), geometry.arrowY(), STAGE_ARROW_WIDTH, STAGE_ARROW_HEIGHT, mouseX, mouseY);
                content.smallNextPageButton().state(hovered, false)
                        .extractRenderState(guiGraphics, geometry.rightX(), geometry.arrowY());
            }
        }

        if (interactive && inside(geometry.textX(), geometry.textY(), geometry.textWidth(), font.lineHeight, mouseX, mouseY)) {
            tooltipSink.accept(stageTooltip(stage, ScholarshipInteractions.player()));
        }
        if (showsCompleteButton(block)) {
            renderCompleteButton(guiGraphics, font, content, block, geometry, mouseX, mouseY, interactive, tooltipSink);
        }
    }

    private void renderCompleteButton(GuiGraphicsExtractor guiGraphics, Font font, BookContentTheme content, ScholarshipBlock block, StageGeometry geometry,
                                      int mouseX, int mouseY, boolean interactive, Consumer<List<Component>> tooltipSink) {
        if (!showsCompleteButton(block)) {
            return;
        }
        var manual = block.stage().manual();
        var player = ScholarshipInteractions.player();
        boolean completing = this.interactions.isCompleting(manual);
        boolean enabled = !completing && player != null && manual.cost().canAfford(player);
        boolean hovered = inCompleteButton(geometry, mouseX, mouseY);
        var sprite = !enabled ? content.completeButtonDisabled() : (hovered ? content.completeButtonHover() : content.completeButtonNormal());
        sprite.extractRenderState(guiGraphics, geometry.buttonX(), geometry.buttonY(), geometry.buttonWidth(), STAGE_LINE_HEIGHT);
        var label = Component.translatable(completing ? Gui.SCHOLARSHIP_STAGE_COMPLETING : Gui.SCHOLARSHIP_STAGE_COMPLETE);
        int labelX = geometry.buttonX() + (geometry.buttonWidth() - font.width(label)) / 2;
        guiGraphics.text(font, label, labelX, geometry.buttonY() + TEXT_INSET_Y, enabled ? STAGE_TEXT_COLOR : COMPLETE_DISABLED_COLOR, false);
        if (interactive && hovered && !completing) {
            tooltipSink.accept(completeTooltip(manual, player));
        }
    }

    private static List<Component> completeTooltip(ScholarshipBlock.ManualStage manual, @Nullable Player player) {
        var lines = new ArrayList<Component>();
        lines.add(Component.translatable(Gui.SCHOLARSHIP_STAGE_COMPLETE_HINT));
        if (player != null) {
            lines.addAll(ScholarshipInteractions.costLines(manual.cost(), player));
        }
        return lines;
    }

    private static List<Component> stageTooltip(ScholarshipBlock.StageLine stage, @Nullable Player player) {
        var lines = new ArrayList<Component>();
        lines.add(stage.title());
        if (stage.description() != null) {
            lines.add(stage.description());
        }
        if (stage.manual() != null) {
            if (!stage.manual().ready()) {
                lines.add(Component.translatable(Gui.SCHOLARSHIP_STAGE_MANUAL).withStyle(ChatFormatting.GRAY));
            }
            if (player != null) {
                lines.addAll(ScholarshipInteractions.costLines(stage.manual().cost(), player));
            }
        }
        return lines;
    }

    /**
     * The rows of the block, in display order: one line per category row, wrapping categories into several lines.
     */
    private static List<Line> lines(ScholarshipBlock block, int x, int top, int width) {
        var lines = new ArrayList<Line>();
        if (block.rows().isEmpty()) {
            return lines;
        }

        int y = contentY(block, top);
        for (var entry : group(block.rows()).entrySet()) {
            var requirements = entry.getValue();
            int count = rowCount(requirements.size(), width);
            //distribute evenly over the rows, so e.g. 11 icons become 6 + 5 rather than 10 + 1
            int perRow = (requirements.size() + count - 1) / count;
            for (int from = 0; from < requirements.size(); from += perRow) {
                lines.add(new Line(entry.getKey(), from == 0, y, requirements.subList(from, Math.min(from + perRow, requirements.size()))));
                y += ROW_HEIGHT;
            }
        }
        return lines;
    }

    private static int rows(ScholarshipBlock block, int width) {
        return lines(block, 0, 0, width).size();
    }

    private static void renderRows(GuiGraphicsExtractor guiGraphics, Font font, BookContentTheme content, ScholarshipBlock block,
                                   int x, int top, int width, int mouseX, int mouseY, boolean interactive,
                                   Consumer<List<Component>> tooltipSink) {
        var lines = lines(block, x, top, width);
        for (var line : lines) {
            renderCategory(guiGraphics, content, line, x, width, mouseX, mouseY, interactive, tooltipSink);
        }

        var slots = layout(block, x, top, width);
        for (var slot : slots) {
            if (interactive && slot.row().action() != null && slot.contains(mouseX, mouseY)) {
                guiGraphics.fill(slot.x() - SLOT_HIGHLIGHT_OUTSET, slot.y() - SLOT_HIGHLIGHT_OUTSET,
                        slot.x() + SLOT_SIZE + SLOT_HIGHLIGHT_OUTSET, slot.y() + SLOT_SIZE + SLOT_HIGHLIGHT_OUTSET, ACTION_HIGHLIGHT_COLOR);
            }
        }
        for (var slot : slots) {
            renderIcon(guiGraphics, font, slot);
        }

        //checks, counts and hover go on top of the icons, so they need their own stratum
        guiGraphics.nextStratum();
        for (var slot : slots) {
            var row = slot.row();
            if (row.met()) {
                content.scholarshipCheck().extractRenderState(guiGraphics, slot.x() + SLOT_SIZE - CHECK_WIDTH, slot.y() + SLOT_SIZE - CHECK_HEIGHT);
            } else if (row.progress() != null) {
                renderCount(guiGraphics, font, slot, String.valueOf(row.progress().target()));
            }
            if (interactive && slot.contains(mouseX, mouseY)) {
                var tooltip = new ArrayList<>(row.tooltip(block.title()));
                var player = ScholarshipInteractions.player();
                if (row.action() != null && player != null) {
                    tooltip.addAll(ScholarshipInteractions.actionTooltip(row, player));
                }
                var grantHint = ScholarshipInteractions.grantHint(row, player);
                if (grantHint != null) {
                    tooltip.add(grantHint);
                }
                tooltipSink.accept(tooltip);
            }
        }
    }

    private static void renderCategory(GuiGraphicsExtractor guiGraphics, BookContentTheme content, Line line, int x, int width,
                                       int mouseX, int mouseY, boolean interactive, Consumer<List<Component>> tooltipSink) {
        int rowY = line.y();
        if (line.first()) {
            var category = line.category();
            int iconY = rowY + (ROW_HEIGHT - CATEGORY_ICON_SIZE) / 2;
            new GuiSprite(category.icon(), CATEGORY_ICON_SIZE, CATEGORY_ICON_SIZE)
                    .extractRenderState(guiGraphics, x, iconY, CATEGORY_ICON_TINT);
            if (interactive && inside(x, iconY, CATEGORY_ICON_SIZE, CATEGORY_ICON_SIZE, mouseX, mouseY)) {
                tooltipSink.accept(categoryTooltip(category));
            }

            int lineX = x + CATEGORY_ICON_SIZE + CATEGORY_LINE_GAP;
            content.scholarshipRowLine().extractRenderState(guiGraphics, lineX, rowY + (ROW_HEIGHT - CATEGORY_LINE_HEIGHT) / 2,
                    x + width - lineX, CATEGORY_LINE_HEIGHT, CATEGORY_LINE_TINT);
        }
    }

    private static List<Component> categoryTooltip(ResearchGroupDefinition category) {
        var lines = new ArrayList<Component>();
        lines.add(category.title());
        category.description().ifPresent(description -> lines.add(description.copy().withStyle(ChatFormatting.GRAY)));
        return lines;
    }

    private static List<Component> separatorTooltip(ScholarshipBlock block) {
        var lines = new ArrayList<Component>();
        lines.add(block.title());
        if (block.description() != null) {
            lines.add(block.description());
        }
        return lines;
    }

    private static void renderIcon(GuiGraphicsExtractor guiGraphics, Font font, Slot slot) {
        var icons = slot.row().icons();
        if (!icons.isEmpty()) {
            icons.get(cycleIndex(icons.size())).render(guiGraphics, font, slot.x(), slot.y());
        }
    }

    /**
     * Draws the target count bottom right of the icon, like the count of an item stack.
     */
    private static void renderCount(GuiGraphicsExtractor guiGraphics, Font font, Slot slot, String count) {
        int countX = slot.x() + COUNT_RIGHT_OFFSET - font.width(count);
        guiGraphics.text(font, count, countX, slot.y() + COUNT_Y_OFFSET, COUNT_COLOR, true);
    }

    /**
     * Cycles the displayed icon once per second, like Thaumonomicon does.
     */
    private static int cycleIndex(int size) {
        if (size <= 1) {
            return 0;
        }
        return (int) ((System.currentTimeMillis() / 1000L) % size);
    }

    private static boolean inside(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
