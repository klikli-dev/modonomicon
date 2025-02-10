/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CategoryListButton extends Button {

    private static final int ANIM_TIME = 5;

    private final BookCategory category;
    private float timeHovered;

    public CategoryListButton(BookCategory category, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, BookEntryScreen.PAGE_WIDTH, 10, Component.translatable(category.getName()), pOnPress, Button.DEFAULT_NARRATION);

        this.category = category;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    private int getEntryColor() {
        return 0x000000;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.active) {
            if (this.isHovered()) {
                this.timeHovered = Math.min(ANIM_TIME, this.timeHovered + ClientTicks.delta);
            } else {
                this.timeHovered = Math.max(0, this.timeHovered - ClientTicks.delta);
            }

            float time = Math.max(0, Math.min(ANIM_TIME, this.timeHovered + (this.isHovered() ? partialTicks : -partialTicks)));
            float widthFract = time / ANIM_TIME;
            boolean locked = !BookUnlockStateManager.get().isUnlockedFor(Minecraft.getInstance().player, this.category);

            guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
            guiGraphics.fill(this.getX() * 2, this.getY() * 2, (this.getX() + (int) ((float) this.width * widthFract)) * 2, (this.getY() + this.height) * 2, 0x22000000);
            RenderSystem.enableBlend();

            if (locked) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
                BookContentRenderer.drawLock(guiGraphics, this.category.getBook(), this.getX() * 2 + 2, this.getY() * 2 + 2);
            } else {
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                this.category.getIcon().render(guiGraphics, this.getX() * 2 + 2, this.getY() * 2 + 2);
            }

            guiGraphics.pose().scale(2F, 2F, 2F);

            MutableComponent name;
            if (locked) {
                name = Component.translatable(Gui.SEARCH_ENTRY_LOCKED);
            } else {
                name = Component.translatable(this.category.getName());
            }

            //apply the custom book font
            name.withStyle(style -> style.withFont(BookDataManager.Client.get().safeFont(this.category.getBook().getFont())));

            int x = this.getX() + 12; //shift right to make space for the icon
            int y = this.getY() + 2;
            int maxWidth = BookEntryScreen.PAGE_WIDTH - 12; //make space for the icon and margin

            guiGraphics.pose().pushPose();

            var scale = Math.min(1.0f, (float) maxWidth / (float) Minecraft.getInstance().font.width(name));
            if (scale < 1) {
                guiGraphics.pose().translate(x - x * scale, y - y * scale, 0);
                guiGraphics.pose().scale(scale, scale, scale);
            }
            guiGraphics.drawString(Minecraft.getInstance().font, name, x, y, this.getEntryColor(), false);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void playDownSound(SoundManager soundHandlerIn) {
        if (this.category != null) {
            //TODO: play flip sound
        }
    }
}
