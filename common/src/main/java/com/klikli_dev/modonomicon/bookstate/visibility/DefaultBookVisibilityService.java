/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visibility;

import com.klikli_dev.modonomicon.book.BookEntryParent;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.world.entity.player.Player;

public class DefaultBookVisibilityService implements BookVisibilityService {

    @Override
    public boolean isUnlocked(Player player, BookEntry entry) {
        return BookUnlockStateManager.get().isUnlockedFor(player, entry);
    }

    @Override
    public EntryDisplayState getEntryDisplayState(Player player, BookEntry entry) {
        if (this.isUnlocked(player, entry)) {
            return EntryDisplayState.UNLOCKED;
        }

        if (!entry.getParents().isEmpty()) {
            var anyParentsUnlocked = false;
            var allParentsUnlocked = true;
            for (BookEntryParent parent : entry.getParents()) {
                if (!BookUnlockStateManager.get().isUnlockedFor(player, parent.getEntry())) {
                    allParentsUnlocked = false;
                } else {
                    anyParentsUnlocked = true;
                }
            }

            if (entry.showWhenAnyParentUnlocked() && !anyParentsUnlocked) {
                return EntryDisplayState.HIDDEN;
            }

            if (!entry.showWhenAnyParentUnlocked() && !allParentsUnlocked) {
                return EntryDisplayState.HIDDEN;
            }
        }

        return entry.hideWhileLocked() ? EntryDisplayState.HIDDEN : EntryDisplayState.LOCKED;
    }

    @Override
    public boolean isVisible(Player player, BookEntry entry) {
        return this.getEntryDisplayState(player, entry).isVisible();
    }
}
