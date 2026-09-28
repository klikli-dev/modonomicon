// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.CommandLink;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.client.gui.TextWrapper;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import com.klikli_dev.modonomicon.client.render.page.PageSplitter;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.util.List;

/**
 * Shared click (hit-testing) and hover handling for book texts.
 * <p>
 * Used both by entry page renderers (via {@code BookPageRenderer}) and by any other screen rendering interactive
 * book texts, such as category and book descriptions on index screens. Rendering and hit-testing must always use
 * the same bounds and scale or links will highlight in a different place than where they are drawn.
 */
public final class BookTextInteraction {

    private BookTextInteraction() {
    }

    /**
     * The minimal context {@link #renderHover(HoverContext, GuiGraphicsExtractor, Style, int, int)} needs.
     * Entry screens provide entry specific tooltip stacks, all other screens render the hover tooltip directly.
     */
    public interface HoverContext {
        Book book();

        Font font();

        int screenWidth();

        void screenWidth(int width);

        List<Component> itemTooltip(ItemStack stack);

        void hoveringItemLink(boolean value);

        /**
         * The entry the hovered text belongs to, or {@code null} if the text is not rendered as part of an
         * entry (e.g. a category or book description). Used to mark entry-restricted command links as unavailable.
         */
        @Nullable
        default Identifier entryId() {
            return null;
        }
    }

    public static float getBookTextHolderScaleForRenderSize(BookTextHolder text, Font font, int width, int height) {
        if (width <= 0 || height <= 0) //this really should not happen, but e.g. on recipe pages with two recipes the getClickedComponentStyle is called despite there being no text and the high textY results in a negative height.
            return 1.0f;

        if (!(text instanceof RenderedBookTextHolder renderedText))
            return 1.0f;

        var cachedScale = BookDataManager.Client.get().getScale(text, width, height);
        if (cachedScale > -1f)
            return cachedScale;

        var components = renderedText.getRenderedText();

        float granularity = 0.01F;
        float scale = 1.0F;
        float totalHeight = 0;
        do {
            //calculate total height by simulating rendering with the current scale.
            //this iterative approach is necessary because when scaling down we fit more words per line, resulting in less lines after wrapping.

            //first scale the width and calculate how many lines we have at this scale
            int totalLines = 0;
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), font);
                totalLines += wrapped.size();
            }

            //then calculate how high the amount of lines would be at this scale
            totalHeight = totalLines * font.lineHeight * scale;

            //now reduce scale for the next iteration
            //it is important to iterate with a fine granularity, otherwise the text will be downscaled way too much
            scale -= granularity;

            //repeat until we have a scale that fits the height
        } while (totalHeight > height);

        BookDataManager.Client.get().putScale(text, width, height, scale);

        return scale;
    }

    @Nullable
    public static Style findClickedStyleAtRenderedLine(Font font, FormattedCharSequence text, float x, float y, double pMouseX, double pMouseY) {
        return findClickedStyleAtRenderedLine(font, text, x, y, pMouseX, pMouseY, new Matrix3x2f());
    }

    @Nullable
    public static Style findClickedStyleAtRenderedLine(Font font, FormattedCharSequence text, float x, float y, double pMouseX, double pMouseY, Matrix3x2f pose) {
        int textX = (int) Math.floor(x);
        int textY = (int) Math.floor(y);
        float xOffset = x - textX;
        float yOffset = y - textY;

        var styleFinder = new ActiveTextCollector.ClickableStyleFinder(font, (int) pMouseX, (int) pMouseY);
        var parameters = new ActiveTextCollector.Parameters(new Matrix3x2f(pose).translate(xOffset, yOffset));
        styleFinder.accept(TextAlignment.LEFT, textX, textY, parameters, text);
        return styleFinder.result();
    }

    @Nullable
    public static Style hitTestTitle(Font font, Book book, BookTextHolder title, int x, int y, double pMouseX, double pMouseY) {
        FormattedCharSequence formattedCharSequence;
        if (title instanceof RenderedBookTextHolder renderedTitle) {
            formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(Component::getVisualOrderText).toList());
        } else {
            if (title.getComponent() == null) {
                //this should not happen, but other errors earlier in the pipeline might cause it.
                Modonomicon.LOG.warn("Title has no component: {}", title);
                return null;
            }

            var fontDescription = new FontDescription.Resource(BookDataManager.Client.get().safeFont(book.getFont()));
            var titleComponent = Component.empty().append(title.getComponent()).withStyle(s -> s.withFont(fontDescription));
            formattedCharSequence = titleComponent.getVisualOrderText();
        }

        float scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) font.width(formattedCharSequence));
        float renderX = x - font.width(formattedCharSequence) * scale / 2.0F;
        float renderY = y + (font.lineHeight * (1 - scale));

        var pose = new Matrix3x2f();
        if (scale < 1) {
            pose.translate(0, y - y * scale);
            pose.scale(scale, scale);
        }

        return findClickedStyleAtRenderedLine(font, formattedCharSequence, renderX, renderY, pMouseX, pMouseY, pose);
    }

    /**
     * Hit-tests a text holder rendered with the given bounds. The split behaviour mirrors
     * {@code BookPageRenderer.renderBookTextHolder(...)}: split pages only expose their first fragment,
     * pages with auto-scaling disabled expose unscaled lines.
     *
     * @param page the page the text belongs to, or {@code null} for texts outside of pages (e.g. descriptions).
     *             Split handling only applies when a page is given.
     * @param book the book the text belongs to, or {@code null} if unknown. Without a book the split continuation
     *             bounds cannot be resolved and split pages fall back to plain hit-testing.
     */
    @Nullable
    public static Style hitTestTextHolder(Font font, @Nullable BookPage page, @Nullable Book book, BookTextHolder text, int x, int y, int width, int height, double pMouseX, double pMouseY) {
        if (page != null && PageSplitter.isSplitEnabled(page) && book != null) {
            var continuationHeight = PageSplitter.continuationBounds(book).height;
            var fragments = PageSplitter.split(text, font, width, height, continuationHeight);
            var first = fragments.isEmpty() ? List.<FormattedCharSequence>of() : fragments.get(0);
            return hitTestFragmentLines(font, first, x, y, pMouseX, pMouseY);
        }
        if (page != null && !PageSplitter.isAutoScaleEnabled(page)) {
            var lines = PageSplitter.wrapForSplit(text, font, width);
            return hitTestFragmentLines(font, lines, x, y, pMouseX, pMouseY);
        }
        if (text.hasComponent()) {
            for (FormattedCharSequence formattedcharsequence : TextWrapper.split(text.getComponent(), width, font)) {
                var style = findClickedStyleAtRenderedLine(font, formattedcharsequence, x, y, pMouseX, pMouseY);
                if (style != null)
                    return style;
                y += font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var scale = getBookTextHolderScaleForRenderSize(text, font, width, height);
            var pose = new Matrix3x2f();
            if (scale < 1) {
                pose.translate(x - x * scale, y - y * scale);
                pose.scale(scale, scale);
            }

            float currentY = y;
            var components = renderedText.getRenderedText();
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, (int) (width / scale), (int) ((width - 10) / scale), font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    var style = findClickedStyleAtRenderedLine(font, formattedcharsequence, x, currentY, pMouseX, pMouseY, pose);
                    if (style != null)
                        return style;
                    currentY += font.lineHeight;
                }
            }
        }

        return null;
    }

    /**
     * Click/hover detection over pre-wrapped fragment lines at scale 1.0,
     * using the exact coordinates used for rendering.
     */
    @Nullable
    public static Style hitTestFragmentLines(Font font, List<FormattedCharSequence> lines, float x, float y, double pMouseX, double pMouseY) {
        float currentY = y;
        for (FormattedCharSequence line : lines) {
            var style = findClickedStyleAtRenderedLine(font, line, x, currentY, pMouseX, pMouseY);
            if (style != null) {
                return style;
            }
            currentY += font.lineHeight;
        }
        return null;
    }

    /**
     * Our copy of guiGraphics.renderComponentHoverEffect(); to handle book links.
     * Call with screen coordinates (not text- or page-local ones).
     */
    public static void renderHover(HoverContext context, GuiGraphicsExtractor guiGraphics, @Nullable Style style, int mouseX, int mouseY) {

        guiGraphics.pose().pushMatrix();
        //TODO: we had a +1000 z translate here
        var newStyle = style;
        if (style != null && style.getHoverEvent() != null) {
            if (style.getHoverEvent().action() == HoverEvent.Action.SHOW_TEXT && style.getHoverEvent() instanceof HoverEvent.ShowText(
                    Component oldComponent
            )) {
                var clickEvent = style.getClickEvent();
                if (clickEvent != null) {
                    if (clickEvent.action() == ClickEvent.Action.OPEN_FILE && clickEvent instanceof ClickEvent.OpenFile(
                            String path
                    )) {
                        //handle book links -> check if locked
                        if (BookLink.isBookLink(path)) {
                            var link = BookLink.from(context.book(), path);
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.entryId != null) {
                                var entry = book.getEntry(link.entryId);

                                Integer page = link.pageNumber;
                                if (link.pageAnchor != null) {
                                    page = entry.getPageNumberForId(link.pageAnchor);
                                }

                                //if locked, append lock warning
                                //handleComponentClicked will prevent the actual click

                                if (!BookServices.visibility().isAccessible(Minecraft.getInstance().player, entry)) {

                                    var newComponent = Component.translatable(
                                            ModonomiconConstants.I18n.Gui.HOVER_BOOK_LINK_LOCKED,
                                            oldComponent,
                                            Component.translatable(ModonomiconConstants.I18n.Gui.HOVER_BOOK_ENTRY_LINK_LOCKED_INFO)
                                                    .withStyle(s -> s.withColor(0xff0015).withBold(true))
                                                    .append("\n")
                                                    .append(
                                                            Component.translatable(
                                                                    ModonomiconConstants.I18n.Gui.HOVER_BOOK_ENTRY_LINK_LOCKED_INFO_HINT,
                                                                    Component.translatable(entry.getCategory().getName())
                                                                            .withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(true))
                                                            ).withStyle(s -> s.withBold(false).withColor(ChatFormatting.WHITE))
                                                    )
                                    );

                                    newStyle = style.withHoverEvent(new HoverEvent.ShowText(newComponent));
                                } else if (page != null && !BookServices.visibility().isAccessible(Minecraft.getInstance().player, entry.getPages().get(page))) {

                                    var newComponent = Component.translatable(
                                            ModonomiconConstants.I18n.Gui.HOVER_BOOK_LINK_LOCKED,
                                            oldComponent,
                                            Component.translatable(ModonomiconConstants.I18n.Gui.HOVER_BOOK_PAGE_LINK_LOCKED_INFO)
                                                    .withStyle(s -> s.withColor(0xff0015).withBold(true))
                                                    .append("\n")
                                                    .append(
                                                            Component.translatable(
                                                                    ModonomiconConstants.I18n.Gui.HOVER_BOOK_PAGE_LINK_LOCKED_INFO_HINT,
                                                                    Component.translatable(entry.getName())
                                                                            .withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(true)),
                                                                    Component.translatable(entry.getCategory().getName())
                                                                            .withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(true))
                                                            ).withStyle(s -> s.withBold(false).withColor(ChatFormatting.WHITE))
                                                    )
                                    );

                                    newStyle = style.withHoverEvent(new HoverEvent.ShowText(newComponent));
                                }
                            }
                        }
                    }

                    if (clickEvent.action() == ClickEvent.Action.RUN_COMMAND && clickEvent instanceof ClickEvent.RunCommand(
                            String command1
                    )) {
                        if (CommandLink.isCommandLink(command1)) {
                            var link = CommandLink.from(context.book(), command1);
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.commandId != null) {
                                var command = book.getCommand(link.commandId);

                                //commands require an entry context and are rejected outside of entries
                                boolean runnable = BookServices.stateAccess().canRun(Minecraft.getInstance().player, command)
                                        && context.entryId() != null;
                                if (!runnable) {
                                    var hoverComponent = Component.translatable(ModonomiconConstants.I18n.Gui.HOVER_COMMAND_LINK_UNAVAILABLE).withStyle(ChatFormatting.RED);
                                    newStyle = style.withHoverEvent(new HoverEvent.ShowText(hoverComponent));
                                    oldComponent = hoverComponent;
                                }

                                if (Minecraft.getInstance().hasShiftDown()) {
                                    var newComponent = oldComponent.copy().append(Component.literal("\n")).append(
                                            Component.literal(command.getCommand()).withStyle(ChatFormatting.GRAY));
                                    newStyle = style.withHoverEvent(new HoverEvent.ShowText(newComponent));
                                }
                            }
                        }
                    }
                }
            }
        }

        style = newStyle;

        //original GuiGraphics.renderComponentHoverEffect(pPoseStack, newStyle, mouseX, mouseY);
        // our own copy of the render code that limits width for the show_text action to not go out of screen
        if (style != null && style.getHoverEvent() != null) {
            switch (style.getHoverEvent()) {
                case HoverEvent.ShowItem(ItemStackTemplate itemstack):
                    //special handling for item link hovers -> itemTooltip appends the recipe lookup hint
                    if (style.getClickEvent() != null)// && ItemLinkRenderer.isItemLink(style.getClickEvent().getValue()))
                        context.hoveringItemLink(true);

                    //temporarily modify width to force forge to handle wrapping correctly
                    var backupWidth = context.screenWidth();
                    context.screenWidth(context.screenWidth() / 2); //not quite sure why exaclty / 2 works, but then forge wrapping handles it correctly on gui scale 3+4
                    var hoveredStack = itemstack.create();
                    guiGraphics.setTooltipForNextFrame(context.font(), context.itemTooltip(hoveredStack), hoveredStack.getTooltipImage(), mouseX, mouseY);
                    context.screenWidth(backupWidth);

                    //then we reset so other item tooltip renders are not affected
                    context.hoveringItemLink(false);

                    break;
                case HoverEvent.ShowEntity(HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo1):
                    HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent$entitytooltipinfo1;
                    if (Minecraft.getInstance().options.advancedItemTooltips) {
                        guiGraphics.setTooltipForNextFrame(context.font(), hoverevent$entitytooltipinfo.getTooltipLines().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                    }
                    break;
                case HoverEvent.ShowText(Component component):
                    //there seem to be cases where tooltip overflows the screen, so we force newlines.
                    var width = context.screenWidth();
                    guiGraphics.setTooltipForNextFrame(context.font(), TextWrapper.split(component, width, context.font()), mouseX, mouseY);
//                    guiGraphics.setTooltipForNextFrame(context.font(), component, mouseX, mouseY);
                    break;
                default:
            }
        }

        guiGraphics.pose().popMatrix();
    }
}
