/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class BookButton extends Button {

    protected final BookContentScreen parent;
    protected final int u, v;
    protected final Supplier<Boolean> displayCondition;
    protected final List<Component> tooltip;

    public BookButton(BookContentScreen parent, int x, int y, int u, int v, int w, int h, Component pMessage, OnPress onPress, Component... tooltip) {
        this(parent, x, y, u, v, w, h, () -> true, pMessage, onPress, tooltip);
    }

    public BookButton(BookContentScreen parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, Component pMessage, OnPress onPress, Component... tooltip) {
        super(x, y, w, h, pMessage, onPress);
        this.parent = parent;
        this.u = u;
        this.v = v;
        this.displayCondition = displayCondition;
        this.tooltip = List.of(tooltip);
    }

    public List<Component> getTooltip() {
        return this.tooltip;
    }

    @Override
    public final void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.active = this.visible = this.displayCondition.get();
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        //if focused we go to the right of our normal button (instead of down, like mc buttons do)
        BookContentScreen.drawFromTexture(ms, this.parent.getBook(), this.x, this.y, this.u + (this.isHoveredOrFocused() ? this.width : 0), this.v, this.width, this.height);
        if (this.isHoveredOrFocused()) {
            this.parent.setTooltip(this.tooltip);
        }
    }
}
