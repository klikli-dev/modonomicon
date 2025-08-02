// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookLink;
import com.klikli_dev.modonomicon.book.CommandLink;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.fluid.FluidHolder;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ContentRenderingScreen {

    default Screen asScreen() {
        return (Screen) this;
    }

    Book getBook();

    BookContentEntry getEntry();

    Font getContentFont();

    Minecraft getMinecraft();

    int getTicksInBook();

    int getBookLeft();

    int getBookTop();

    void setTooltipStack(ItemStack stack);

    void setTooltipStack(FluidHolder stack);

    boolean isHoveringItemLink();

    void isHoveringItemLink(boolean value);

    default boolean isMouseInRange(double absMx, double absMy, int x, int y, int w, int h) {
        double mx = absMx;
        double my = absMy;

        return mx > x && my > y && mx <= (x + w) && my <= (y + h);
    }

    default void renderItemStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.renderItem(stack, x, y);
        guiGraphics.renderItemDecorations(this.getContentFont(), stack, x, y);

        if (this.isMouseInRange(mouseX, mouseY, x, y, 16, 16)) {
            this.setTooltipStack(stack);
        }
    }

    default void renderItemStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, stacks, -1);
    }

    default void renderItemStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks, int countOverride) {
        var filteredStacks = PageRendererRegistry.filterRenderableItemStacks(stacks);
        if (filteredStacks.size() > 0) {
            var currentStack = filteredStacks.get((this.getTicksInBook() / 20) % filteredStacks.size());
            this.renderItemStack(guiGraphics, x, y, mouseX, mouseY, countOverride > 0 ? currentStack.copyWithCount(countOverride) : currentStack);
        }
    }

    default void renderIngredient(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, ingr.display().resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level)), -1);
    }

    default void renderIngredient(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr, int countOverride) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, ingr.display().resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level)), countOverride);
    }

    default void renderFluidStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, FluidHolder stack) {
        this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, stack, FluidHolder.BUCKET_VOLUME);
    }

    default void renderFluidStack(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, FluidHolder stack, int capacity) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        ClientServices.FLUID.drawFluid(guiGraphics, 18, 18, stack, capacity, x, y);
        guiGraphics.pose().popMatrix();

        if (this.isMouseInRange(mouseX, mouseY, x, y, 18, 18)) {
            this.setTooltipStack(stack);
        }
    }

    default void renderFluidStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidHolder> stacks) {
        this.renderFluidStacks(guiGraphics, x, y, mouseX, mouseY, stacks, FluidHolder.BUCKET_VOLUME);
    }

    default void renderFluidStacks(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidHolder> stacks, int capacity) {
        var filteredStacks = PageRendererRegistry.filterRenderableFluidStacks(stacks);
        if (filteredStacks.size() > 0) {
            this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, filteredStacks.get((this.getTicksInBook() / 20) % filteredStacks.size()), capacity);
        }
    }

    /**
     * Our copy of guiGraphics.renderComponentHoverEffect(); to handle book links
     */
    default void renderComponentHoverEffect(GuiGraphics guiGraphics, @Nullable Style style, int mouseX, int mouseY) {

        guiGraphics.pose().pushMatrix();
        //TODO: we had a +1000 z translate here
        var newStyle = style;
        if (style != null && style.getHoverEvent() != null) {
            if (style.getHoverEvent().action() == HoverEvent.Action.SHOW_TEXT && style.getHoverEvent() instanceof HoverEvent.ShowText showText) {
                var clickEvent = style.getClickEvent();
                if (clickEvent != null) {
                    if (clickEvent.action() == ClickEvent.Action.OPEN_FILE && clickEvent instanceof ClickEvent.OpenFile openFile) {
                        //handle book links -> check if locked
                        if (BookLink.isBookLink(openFile.path())) {
                            var link = BookLink.from(this.getBook(), openFile.path());
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.entryId != null) {
                                var entry = book.getEntry(link.entryId);

                                Integer page = link.pageNumber;
                                if (link.pageAnchor != null) {
                                    page = entry.getPageNumberForAnchor(link.pageAnchor);
                                }

                                //if locked, append lock warning
                                //handleComponentClicked will prevent the actual click

                                if (!BookUnlockStateManager.get().isUnlockedFor(Minecraft.getInstance().player, entry)) {
                                    var oldComponent = showText.value();

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
                                } else if (page != null && !BookUnlockStateManager.get().isUnlockedFor(Minecraft.getInstance().player, entry.getPages().get(page))) {
                                    var oldComponent = showText.value();

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

                    if (clickEvent.action() == ClickEvent.Action.RUN_COMMAND && clickEvent instanceof ClickEvent.RunCommand runCommand) {
                        if (CommandLink.isCommandLink(runCommand.command())) {
                            var link = CommandLink.from(this.getBook(), runCommand.command());
                            var book = BookDataManager.get().getBook(link.bookId);
                            if (link.commandId != null) {
                                var command = book.getCommand(link.commandId);

                                var oldComponent = showText.value();

                                if (!BookUnlockStateManager.get().canRunFor(Minecraft.getInstance().player, command)) {
                                    var hoverComponent = Component.translatable(ModonomiconConstants.I18n.Gui.HOVER_COMMAND_LINK_UNAVAILABLE).withStyle(ChatFormatting.RED);
                                    newStyle = style.withHoverEvent(new HoverEvent.ShowText(hoverComponent));
                                    oldComponent = hoverComponent;
                                }

                                if (Screen.hasShiftDown()) {
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
                case HoverEvent.ShowItem(ItemStack itemstack):
                    //special handling for item link hovers -> we append another line in this.getTooltipFromItem
                    if (style.getClickEvent() != null)// && ItemLinkRenderer.isItemLink(style.getClickEvent().getValue()))
                        this.isHoveringItemLink(true);

                    //temporarily modify width to force forge to handle wrapping correctly
                    var backupWidth = this.asScreen().width;
                    this.asScreen().width = this.asScreen().width / 2; //not quite sure why exaclty / 2 works, but then forge wrapping handles it correctly on gui scale 3+4
                    guiGraphics.setTooltipForNextFrame(this.getContentFont(), itemstack, mouseX, mouseY);
                    this.asScreen().width = backupWidth;

                    //then we reset so other item tooltip renders are not affected
                    this.isHoveringItemLink(false);

                    break;
                case HoverEvent.ShowEntity(HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo1):
                    HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent$entitytooltipinfo1;
                    if (this.getMinecraft().options.advancedItemTooltips) {
                        guiGraphics.setTooltipForNextFrame(this.getContentFont(), hoverevent$entitytooltipinfo.getTooltipLines().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                    }
                    break;
                case HoverEvent.ShowText(Component component):
                    //      guiGraphics.renderTooltip(this.getFont(), this.getFont().split(component, Math.max(guiGraphics.guiWidth() / 2, 200)), mouseX, mouseY); //render call with original width calc
                    var width = (this.asScreen().width / 2) - mouseX - 10; //our own width calc
                    guiGraphics.setTooltipForNextFrame(this.getContentFont(), this.getContentFont().split(component, width), mouseX, mouseY);
                    break;
                default:
            }
        }

        guiGraphics.pose().popMatrix();
    }
}
