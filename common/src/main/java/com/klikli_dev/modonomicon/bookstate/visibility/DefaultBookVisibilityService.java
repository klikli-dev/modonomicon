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
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DefaultBookVisibilityService implements BookVisibilityService {

    @Override
    public boolean isVisible(Player player, BookCategory category) {
        if (category == null || category.getBook() == null) {
            //Category not linked to a book (yet): book not built after /reload or loading failed. Fail closed, never crash a tick.
            Modonomicon.LOG.error("Checked visibility for a category with null book (category: '{}'). The book was likely not built yet (e.g. during /reload) or failed to load. Returning false to avoid a crash.", category == null ? null : category.getId());
            return false;
        }
        return category.getCondition().test(BookConditionContext.of(category.getBook(), category), player);
    }

    @Override
    public boolean isVisible(Player player, BookEntry entry) {
        if (entry == null || entry.getBook() == null) {
            Modonomicon.LOG.error("Checked visibility for an entry with null book (entry: '{}'). The book was likely not built yet (e.g. during /reload) or failed to load. Returning false to avoid a crash.", entry == null ? null : entry.getId());
            return false;
        }
        return entry.getCondition().test(BookConditionContext.of(entry.getBook(), entry), player);
    }

    @Override
    public boolean isVisible(Player player, BookPage page) {
        if (page == null || page.getBook() == null) {
            Modonomicon.LOG.error("Checked visibility for a page with null book. The book was likely not built yet (e.g. during /reload) or failed to load. Returning false to avoid a crash.");
            return false;
        }
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
