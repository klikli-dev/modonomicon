/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class EntryReadEventJS extends ServerEventJS {
    ServerPlayer player;
    BookEntry entry;

    public EntryReadEventJS(ServerPlayer player, BookEntry entry) {
        super(player.getServer());
        this.player = player;
        this.entry = entry;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public Book getBook() {
        return this.entry.getBook();
    }

    public BookEntry getEntry() {
        return this.entry;
    }
}
