/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.kubejs;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class EntryUnlockedEventJS extends ServerEventJS {

    ServerPlayer player;
    BookConditionEntryContext unlockedContent;

    public EntryUnlockedEventJS(ServerPlayer player, BookConditionEntryContext unlockedContent) {
        super(player.getServer());
        this.player = player;
        this.unlockedContent = unlockedContent;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public BookConditionEntryContext getUnlockedContent() {
        return this.unlockedContent;
    }

    public Book getBook() {
        return this.unlockedContent.getBook();
    }

    public BookEntry getEntry() {
        return this.unlockedContent.getEntry();
    }

    public BookCondition getCondition() {
        return this.unlockedContent.getEntry().getCondition();
    }
}
