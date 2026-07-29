/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiButtonSprites;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BookButton extends Button {

    protected final BookScreenWithButtons parent;
    protected final Function<BookTheme, GuiButtonSprites> sprites;
    protected final Supplier<Boolean> displayCondition;
    protected final List<Component> tooltip;

    public BookButton(BookScreenWithButtons parent, int x, int y, int w, int h, Function<BookTheme, GuiButtonSprites> sprites, Component pMessage, OnPress onPress, Component... tooltip) {
        this(parent, x, y, w, h, sprites, () -> true, pMessage, onPress, tooltip);
    }

    public BookButton(BookScreenWithButtons parent, int x, int y, int w, int h, Function<BookTheme, GuiButtonSprites> sprites, Supplier<Boolean> displayCondition, Component pMessage, OnPress onPress, Component... tooltip) {
        super(x, y, w, h, pMessage, onPress, Button.DEFAULT_NARRATION);
        this.parent = parent;
        this.sprites = sprites;
        this.displayCondition = displayCondition;
        this.tooltip = List.of(tooltip);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        this.active = this.displayCondition.get();
        if (!this.active) return;

        BookContentRenderer.drawButton(guiGraphics, this.sprites.apply(this.parent.getBook().theme()), this.getX(), this.getY(), this.isHovered());
        if (this.isHovered()) {
            this.parent.setTooltip(this.tooltip);
        }
    }
}
