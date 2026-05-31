/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.interaction;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.access.BookStateAccess;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
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
        var firstRead = this.stateAccess.markEntryRead(player, entry);
        this.stateAccess.setEntryUnread(player, entry, false);
        return firstRead;
    }

    @Override
    public boolean markCategoryRead(ServerPlayer player, BookCategory category) {
        var firstRead = this.stateAccess.markCategoryRead(player, category);
        this.stateAccess.setCategoryUnread(player, category, false);
        return firstRead;
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

    public void updateVisibilityDrivenUnread(ServerPlayer player, com.klikli_dev.modonomicon.book.Book book, BookVisibilitySnapshots before, BookVisibilitySnapshots after) {
        after.entries().stream().filter(entryId -> !before.entries().contains(entryId)).forEach(entryId -> this.stateAccess.setEntryUnread(player, book.getEntry(entryId), true));
        after.categories().stream().filter(categoryId -> !before.categories().contains(categoryId)).forEach(categoryId -> this.stateAccess.setCategoryUnread(player, book.getCategory(categoryId), true));
    }
}
