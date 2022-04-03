/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ConditionalButton extends Button {

    final BookContentScreen parent;
    final int u, v;
    final Supplier<Boolean> displayCondition;

    public ConditionalButton(BookContentScreen parent, int x, int y, int u, int v, int w, int h, Component pMessage, OnPress onPress) {
        this(parent, x, y, u, v, w, h, () -> true, pMessage, onPress);
    }

    public ConditionalButton(BookContentScreen parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, Component pMessage, OnPress onPress) {
        super(x, y, w, h, pMessage, onPress);
        this.parent = parent;
        this.u = u;
        this.v = v;
        this.displayCondition = displayCondition;
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
    }

}
