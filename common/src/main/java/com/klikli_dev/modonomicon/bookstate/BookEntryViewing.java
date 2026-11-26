/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.api.events.EntryFirstReadEvent;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.server.level.ServerPlayer;

/**
 * The server side of opening an entry: marks it read and fires its entry viewed research hooks.
 * Used when a player opens an entry, and when a game master satisfies a requirement that views an entry.
 */
public final class BookEntryViewing {

    private BookEntryViewing() {
    }

    /**
     * Marks the entry read if needed, replays the view hook (so research can be rebuilt after a research reset) and syncs the changed states.
     */
    public static void view(ServerPlayer player, BookEntry entry) {
        var before = BookVisibilitySnapshots.collect(player, entry.getBook());
        var wasUnread = BookServices.interaction().isEntryUnread(player, entry);
        var firstRead = BookServices.interaction().markEntryRead(player, entry);
        var researchChanged = ResearchServices.hooks().onEntryViewedOnce(player, entry.getId());
        if (researchChanged) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, entry.getBook(), before, BookVisibilitySnapshots.collect(player, entry.getBook()));
            ResearchServices.state().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        } else if (firstRead || wasUnread) {
            BookVisualStateManager.get().syncFor(player);
        }
        if (firstRead) {
            ModonomiconEvents.server().entryFirstRead(new EntryFirstReadEvent(entry.getBook().getId(), entry.getId()));
        }
    }
}
