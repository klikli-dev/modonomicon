/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.interaction;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.access.BookStateAccess;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class DefaultBookInteractionService implements BookInteractionService {

    private final BookStateAccess stateAccess;

    public DefaultBookInteractionService(BookStateAccess stateAccess) {
        this.stateAccess = stateAccess;
    }

    @Override
    public boolean markEntryRead(ServerPlayer player, BookEntry entry) {
        if (!this.stateAccess.markEntryRead(player, entry)) {
            return false;
        }

        this.stateAccess.setEntryUnread(player, entry, false);
        return true;
    }

    @Override
    public boolean markCategoryRead(ServerPlayer player, BookCategory category) {
        if (!this.stateAccess.markCategoryRead(player, category)) {
            return false;
        }

        this.stateAccess.setCategoryUnread(player, category, false);
        return true;
    }

    @Override
    public boolean isEntryRead(Player player, BookEntry entry) {
        return this.stateAccess.isEntryRead(player, entry);
    }

    @Override
    public boolean isCategoryRead(Player player, BookCategory category) {
        return this.stateAccess.isCategoryRead(player, category);
    }

    @Override
    public boolean isCategoryUnread(Player player, BookCategory category) {
        return this.stateAccess.isCategoryUnread(player, category);
    }

    @Override
    public boolean isEntryUnread(Player player, BookEntry entry) {
        return this.stateAccess.isEntryUnread(player, entry);
    }
}
