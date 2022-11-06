/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.Supplier;


public class ReadAllButton extends Button {

    public static final int U = 300;
    public static final int V = 21;
    public static final int HEIGHT = 16;
    public static final int WIDTH = 16;

    private final BookOverviewScreen parent;

    private MutableComponent tooltip;
    private Supplier<Boolean> displayCondition;

    public ReadAllButton(BookOverviewScreen parent, int x, int y, Supplier<Boolean> displayCondition, OnPress onPress, OnTooltip onTooltip) {
        super(x, y,WIDTH, HEIGHT,
                new TranslatableComponent(Gui.BUTTON_READ_ALL),
                onPress, onTooltip
        );
        this.parent = parent;
        this.tooltip =  new TranslatableComponent(Gui.BUTTON_READ_ALL_TOOLTIP);
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

        ms.pushPose();
        ms.translate(0, 0, 200);
        BookContentScreen.drawFromTexture(ms, this.parent.getBook(), this.x, this.y, U + (this.isHoveredOrFocused() ? this.width : 0), V, this.width, this.height);
        ms.popPose();
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(ms, mouseX, mouseY);
        }
    }

    public MutableComponent getTooltip() {
        return tooltip;
    }
}
