/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visibility;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.BookEntryParent;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DefaultBookVisibilityService implements BookVisibilityService {

    @Override
    public boolean isVisible(Player player, BookCategory category) {
        return category.getCondition().test(BookConditionContext.of(category.getBook(), category), player);
    }

    @Override
    public boolean isVisible(Player player, BookEntry entry) {
        return entry.getCondition().test(BookConditionContext.of(entry.getBook(), entry), player);
    }

    @Override
    public boolean isVisible(Player player, BookPage page) {
        return page.getCondition().test(BookConditionContext.of(page.getBook(), page), player);
    }

    @Override
    public boolean isAccessible(Player player, BookEntry entry) {
        return this.isVisible(player, entry);
    }

    @Override
    public boolean isAccessible(Player player, BookPage page) {
        return this.isVisible(player, page);
    }

    @Override
    public List<BookPage> getVisiblePages(Player player, BookEntry entry) {
        return entry.getPages().stream().filter(page -> this.isVisible(player, page)).toList();
    }

    @Override
    public EntryDisplayState getEntryDisplayState(Player player, BookEntry entry) {
        if (this.isVisible(player, entry)) {
            return EntryDisplayState.UNLOCKED;
        }

        if (!entry.getParents().isEmpty()) {
            var anyParentsUnlocked = false;
            var allParentsUnlocked = true;
            for (BookEntryParent parent : entry.getParents()) {
                if (!this.isVisible(player, parent.getEntry())) {
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
}
