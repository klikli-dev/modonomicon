// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.events;

import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * An event that is fired on both the client and server side, when an entry is read for the first time.
 * Note that resetting the book and then reading the entry again, will trigger this event again.
 *
 * If you are e.g. awarding rewards based on this event you should save somewhere that the player already received the entry.
 * See e.g. {@link BookCommand#execute(ServerPlayer)} which stores how many times a command has been executed.
 *
 * Further, any rewards or persistent game logic should only be done on the server side call of this event.
 */
public class EntryFirstReadEvent extends ModonomiconEvent {
    protected Identifier bookId;
    protected Identifier entryId;

    public EntryFirstReadEvent(Identifier bookId, Identifier entryId) {
        super(false);

        this.bookId = bookId;
        this.entryId = entryId;
    }

    public Identifier getBookId() {
        return this.bookId;
    }

    public Identifier getEntryId() {
        return this.entryId;
    }
}
