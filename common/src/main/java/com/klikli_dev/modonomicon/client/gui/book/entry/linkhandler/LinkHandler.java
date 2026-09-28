// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class LinkHandler {

    protected LinkClickContext context;

    public LinkHandler(LinkClickContext context) {
        this.context = context;
    }

    public LinkClickContext context() {
        return this.context;
    }

    public Book book() {
        return this.context.book();
    }

    public Player player() {
        return this.context.player();
    }

    public abstract ClickResult handleClick(@NotNull Style pStyle);

    public enum ClickResult {
        SUCCESS,
        FAILURE,
        UNHANDLED
    }
}
