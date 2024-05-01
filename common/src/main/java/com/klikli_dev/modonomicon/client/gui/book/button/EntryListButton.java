/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.entries.*;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookSearchScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EntryListButton extends Button {

    private static final int ANIM_TIME = 5;

    private final BookSearchScreen parent;
    private final BookEntry entry;
    private float timeHovered;

    public EntryListButton(BookSearchScreen parent, BookEntry entry, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, BookContentScreen.PAGE_WIDTH, 10, Component.translatable(entry.getName()), pOnPress, Button.DEFAULT_NARRATION);

        this.parent = parent;
        this.entry = entry;
    }

    public BookEntry getEntry() {
        return this.entry;
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
            boolean locked = !BookUnlockStateManager.get().isUnlockedFor(Minecraft.getInstance().player, this.entry);

            guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
            guiGraphics.fill(this.getX() * 2, this.getY() * 2, (this.getX() + (int) ((float) this.width * widthFract)) * 2, (this.getY() + this.height) * 2, 0x22000000);
            RenderSystem.enableBlend();

            if (locked) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
                BookContentScreen.drawLock(guiGraphics, this.parent.getParentScreen().getBook(), this.getX() * 2 + 2, this.getY() * 2 + 2);
            } else {
                this.entry.getIcon().render(guiGraphics, this.getX() * 2 + 2, this.getY() * 2 + 2);
            }

            guiGraphics.pose().scale(2F, 2F, 2F);

            MutableComponent name;
            if (locked) {
                name = Component.translatable(Gui.SEARCH_ENTRY_LOCKED);
            } else {
                name = Component.translatable(this.entry.getName());
            }

            //TODO: if we ever add a font style setting to the book, use it here
            guiGraphics.drawString(Minecraft.getInstance().font, name, this.getX() + 12, this.getY(), this.getEntryColor(), false);
        }
    }

    @Override
    public void playDownSound(SoundManager soundHandlerIn) {
        if (this.entry != null) {
            //TODO: play flip sound
        }
    }


}
