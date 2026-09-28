// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.BookTextInteraction;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.fluid.FluidHolder;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface ContentRenderingScreen extends BookTextInteraction.HoverContext {

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

    List<Component> getTooltipFromItem(ItemStack stack);

    boolean isHoveringItemLink();

    void isHoveringItemLink(boolean value);

    @Override
    default int screenWidth() {
        return this.asScreen().width;
    }

    @Override
    default void screenWidth(int width) {
        this.asScreen().width = width;
    }

    @Override
    default List<Component> itemTooltip(ItemStack stack) {
        return this.getTooltipFromItem(stack);
    }

    @Override
    default void hoveringItemLink(boolean value) {
        this.isHoveringItemLink(value);
    }

    @Override
    default Identifier entryId() {
        return this.getEntry().getId();
    }

    default boolean isMouseInRange(double absMx, double absMy, int x, int y, int w, int h) {
        double mx = absMx;
        double my = absMy;

        return mx > x && my > y && mx <= (x + w) && my <= (y + h);
    }

    default void renderItemStack(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.item(stack, x, y);
        guiGraphics.itemDecorations(this.getContentFont(), stack, x, y);

        if (this.isMouseInRange(mouseX, mouseY, x, y, 16, 16)) {
            this.setTooltipStack(stack);
        }
    }

    default void renderItemStacks(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, stacks, -1);
    }

    default void renderItemStacks(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Collection<ItemStack> stacks, int countOverride) {
        var filteredStacks = PageRendererRegistry.filterRenderableItemStacks(stacks);
        if (filteredStacks.size() > 0) {
            var currentStack = filteredStacks.get((this.getTicksInBook() / 20) % filteredStacks.size());
            this.renderItemStack(guiGraphics, x, y, mouseX, mouseY, countOverride > 0 ? currentStack.copyWithCount(countOverride) : currentStack.copy());
        }
    }

    default void renderIngredient(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, ingr.display().resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level)), -1);
    }

    default void renderIngredient(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingr, int countOverride) {
        this.renderItemStacks(guiGraphics, x, y, mouseX, mouseY, ingr.display().resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level)), countOverride);
    }

    default void renderFluidStack(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, FluidHolder stack) {
        this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, stack, FluidHolder.BUCKET_VOLUME);
    }

    default void renderFluidStack(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, FluidHolder stack, int capacity) {
        if (stack.isEmpty() || !PageRendererRegistry.isRenderable(stack)) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        drawFluid(guiGraphics, 18, 18, stack, capacity, x, y);
        guiGraphics.pose().popMatrix();

        if (this.isMouseInRange(mouseX, mouseY, x, y, 18, 18)) {
            this.setTooltipStack(stack);
        }
    }

    default void renderFluidStacks(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidHolder> stacks) {
        this.renderFluidStacks(guiGraphics, x, y, mouseX, mouseY, stacks, FluidHolder.BUCKET_VOLUME);
    }

    default void renderFluidStacks(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Collection<FluidHolder> stacks, int capacity) {
        var filteredStacks = PageRendererRegistry.filterRenderableFluidStacks(stacks);
        if (filteredStacks.size() > 0) {
            this.renderFluidStack(guiGraphics, x, y, mouseX, mouseY, filteredStacks.get((this.getTicksInBook() / 20) % filteredStacks.size()), capacity);
        }
    }

    private static void drawFluid(GuiGraphicsExtractor guiGraphics, int width, int height, FluidHolder fluidHolder, int capacity, int x, int y) {
        if (fluidHolder.isEmpty() || fluidHolder.getFluid().value().isSame(Fluids.EMPTY) || capacity <= 0) {
            return;
        }

        var fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidHolder.getFluid().value().defaultFluidState());
        var sprite = fluidModel.stillMaterial().sprite();
        if (sprite == null) {
            return;
        }

        int amount = fluidHolder.getAmount();
        int scaledAmount = (amount * height) / capacity;
        if (amount > 0 && scaledAmount < 1) {
            scaledAmount = 1;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }
        if (scaledAmount <= 0) {
            return;
        }

        SpriteContents spriteContents = sprite.contents();

        int renderY = y + height - scaledAmount;
        guiGraphics.enableScissor(x, renderY, x + width, renderY + scaledAmount);
        try {
            guiGraphics.blitTiledSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    x,
                    renderY,
                    width,
                    scaledAmount,
                    0,
                    0,
                    spriteContents.width(),
                    spriteContents.height(),
                    spriteContents.width(),
                    spriteContents.height(),
                    ClientServices.FLUID.getColorTint(fluidHolder)
            );
        } finally {
            guiGraphics.disableScissor();
        }
    }

    /**
     * Our copy of guiGraphics.renderComponentHoverEffect(); to handle book links
     */
    default void renderComponentHoverEffect(GuiGraphicsExtractor guiGraphics, @Nullable Style style, int mouseX, int mouseY) {
        BookTextInteraction.renderHover(this, guiGraphics, style, mouseX, mouseY);
    }
}
