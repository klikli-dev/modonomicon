/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Supplier;


public class ReadAllButton extends Button {

    public static final int U = 0;
    public static final int V_READ_UNLOCKED = 196;
    public static final int V_READ_ALL = 210;
    public static final int V_NONE = 224;
    public static final int WIDTH = 16;
    public static final int HEIGHT = 14;


    private final BookOverviewScreen parent;

    private final MutableComponent tooltipReadUnlocked;
    private final MutableComponent tooltipReadAll;
    private final MutableComponent tooltipNone;
    private final MutableComponent tooltipShiftInstructions;
    private final MutableComponent tooltipShiftWarning;
    private final Supplier<Boolean> displayCondition;
    private final Supplier<Boolean> hasUnreadUnlockedEntries;


    public ReadAllButton(BookOverviewScreen parent, int x, int y, Supplier<Boolean> hasUnreadUnlockedEntries, Supplier<Boolean> displayCondition, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(Gui.BUTTON_READ_ALL),
                onPress, onTooltip
        );
        this.parent = parent;
        this.tooltipReadUnlocked = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_UNLOCKED);
        this.tooltipReadAll = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_ALL);
        this.tooltipNone = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_NONE);
        this.tooltipShiftInstructions = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_INSTRUCTIONS);
        this.tooltipShiftWarning = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_WARNING);
        this.hasUnreadUnlockedEntries = hasUnreadUnlockedEntries;
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
        var hovered = this.isHoveredOrFocused();

        int u = U;

        //by default we show green if we can read unlocked entries or gray if none
        //if shift is down we offer to mark all as read
        //if neither is possible the button should be hidden which is handled by BookOverviewScreen#canSeeReadAllButton
        int v = this.hasUnreadUnlockedEntries.get() ? V_READ_UNLOCKED : V_NONE;

        if (hovered)
            u += this.width; //shift to the right for hover variant

        if (Screen.hasShiftDown()) {
            v = V_READ_ALL;
        }

        RenderSystem.setShaderTexture(0, this.parent.getBook().getBookOverviewTexture());
        blit(ms, this.x, this.y, u, v, this.width, this.height, 256, 256);
        ms.popPose();

        if (hovered) {
            this.renderToolTip(ms, mouseX, mouseY);
        }
    }

    public List<MutableComponent> getTooltips() {

        if (Screen.hasShiftDown()) {
            return List.of(
                    this.tooltipReadAll,
                    Component.empty(),
                    this.tooltipShiftWarning
            );
        }

        if (this.hasUnreadUnlockedEntries.get()) {
            return List.of(
                    this.tooltipReadUnlocked,
                    Component.empty(),
                    this.tooltipShiftInstructions
            );
        }

        return List.of(
                this.tooltipNone,
                Component.empty(),
                this.tooltipShiftInstructions
        );
    }
}
