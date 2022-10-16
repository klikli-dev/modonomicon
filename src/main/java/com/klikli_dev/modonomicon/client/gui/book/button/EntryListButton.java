/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */


package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EntryListButton extends Button {

    private static final int ANIM_TIME = 5;

    private final BookOverviewScreen parent;
    private final BookEntry entry;
    private float timeHovered;

    public EntryListButton(BookOverviewScreen parent, BookEntry entry, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, BookContentScreen.PAGE_WIDTH, 10, Component.translatable(entry.getName()), pOnPress);

        this.parent = parent;
        this.entry = entry;
    }

    public BookEntry getEntry() {
        return entry;
    }

    private int getEntryColor() {
        return 0x000000;
    }

    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (active) {
            if (isHoveredOrFocused()) {
                timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicks.delta);
            } else {
                timeHovered = Math.max(0, timeHovered - ClientTicks.delta);
            }

            float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
            float widthFract = time / ANIM_TIME;
            boolean locked = !BookUnlockCapability.isUnlockedFor(Minecraft.getInstance().player, entry);

            ms.scale(0.5F, 0.5F, 0.5F);
            GuiComponent.fill(ms, x * 2, y * 2, (x + (int) ((float) width * widthFract)) * 2, (y + height) * 2, 0x22000000);
            RenderSystem.enableBlend();

            if (locked) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
                BookContentScreen.drawLock(ms, parent.getBook(), x * 2 + 2, y * 2 + 2);
            } else {
                entry.getIcon().render(ms, x * 2 + 2, y * 2 + 2);
            }

            ms.scale(2F, 2F, 2F);

            MutableComponent name;
            if (locked) {
                name = Component.translatable(Gui.SEARCH_ENTRY_LOCKED);
            } else {
                name = Component.translatable(entry.getName());
            }

            //TODO: if we ever add a font style setting to the book, use it here
            Minecraft.getInstance().font.draw(ms, name, x + 12, y, getEntryColor());
        }
    }

    @Override
    public void playDownSound(SoundManager soundHandlerIn) {
        if (entry != null) {
            //TODO: play flip sound
        }
    }
}
