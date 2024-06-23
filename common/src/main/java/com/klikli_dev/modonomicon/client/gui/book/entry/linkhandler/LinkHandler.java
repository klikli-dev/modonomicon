// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class LinkHandler {

    protected BookEntryScreen screen;

    public LinkHandler(BookEntryScreen screen) {
        this.screen = screen;
    }

    public BookEntryScreen screen() {
        return this.screen;
    }

    public Book book() {
        return this.screen().getBook();
    }

    public BookCategory category() {
        return this.screen().getEntry().getCategory();
    }

    public BookContentEntry entry() {
        return this.screen().getEntry();
    }

    public Player player() {
        return Minecraft.getInstance().player;
    }

    public abstract ClickResult handleClick(@NotNull Style pStyle);

    public enum ClickResult {
        SUCCESS,
        FAILURE,
        UNHANDLED
    }
}