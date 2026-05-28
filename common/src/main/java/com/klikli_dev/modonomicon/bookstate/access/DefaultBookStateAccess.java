/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.access;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class DefaultBookStateAccess implements BookStateAccess {

    @Override
    public boolean isUnlocked(Player player, BookCategory category) {
        return BookUnlockStateManager.get().isUnlockedFor(player, category);
    }

    @Override
    public boolean isUnlocked(Player player, BookEntry entry) {
        return BookUnlockStateManager.get().isUnlockedFor(player, entry);
    }

    @Override
    public boolean isUnlocked(Player player, BookPage page) {
        return BookUnlockStateManager.get().isUnlockedFor(player, page);
    }

    @Override
    public List<BookPage> getUnlockedPages(Player player, BookEntry entry) {
        return BookUnlockStateManager.get().getUnlockedPagesFor(player, entry);
    }

    @Override
    public boolean isEntryRead(Player player, BookEntry entry) {
        return BookUnlockStateManager.get().isReadFor(player, entry);
    }

    @Override
    public boolean isCategoryRead(Player player, BookCategory category) {
        return BookUnlockStateManager.get().isCategoryReadFor(player, category);
    }

    @Override
    public boolean canRun(Player player, BookCommand command) {
        return BookUnlockStateManager.get().canRunFor(player, command);
    }

    @Override
    public void setRun(Player player, BookCommand command) {
        BookUnlockStateManager.get().setRunFor(player, command);
    }

    @Override
    public boolean markEntryRead(ServerPlayer player, BookEntry entry) {
        return BookUnlockStateManager.get().readFor(player, entry);
    }

    @Override
    public boolean markCategoryRead(ServerPlayer player, BookCategory category) {
        return BookUnlockStateManager.get().readCategoryFor(player, category);
    }

    @Override
    public void updateAndSync(ServerPlayer player) {
        BookUnlockStateManager.get().updateAndSyncFor(player);
    }

    @Override
    public void sync(ServerPlayer player) {
        BookUnlockStateManager.get().syncFor(player);
    }

    @Override
    public Map<Identifier, Long> getUnlockTimestamps(Player player, Book book) {
        return BookUnlockStateManager.get().getUnlockTimestampsFor(player, book);
    }

    @Override
    public BookVisualState getBookVisualState(Player player, Book book) {
        return BookVisualStateManager.get().getBookStateFor(player, book);
    }

    @Override
    public CategoryVisualState getCategoryVisualState(Player player, BookCategory category) {
        return BookVisualStateManager.get().getCategoryStateFor(player, category);
    }

    @Override
    public EntryVisualState getEntryVisualState(Player player, BookEntry entry) {
        return BookVisualStateManager.get().getEntryStateFor(player, entry);
    }

    @Override
    public boolean isEntryUnread(Player player, BookEntry entry) {
        return BookVisualStateManager.get().isEntryUnreadFor(player, entry);
    }

    @Override
    public boolean isCategoryUnread(Player player, BookCategory category) {
        return BookVisualStateManager.get().isCategoryUnreadFor(player, category);
    }

    @Override
    public void setCategoryUnread(ServerPlayer player, BookCategory category, boolean unread) {
        BookVisualStateManager.get().setCategoryUnreadFor(player, category, unread);
    }

    @Override
    public void setEntryVisualState(ServerPlayer player, BookEntry entry, EntryVisualState state) {
        BookVisualStateManager.get().setEntryStateFor(player, entry, state);
    }

    @Override
    public void setEntryUnread(ServerPlayer player, BookEntry entry, boolean unread) {
        BookVisualStateManager.get().setEntryUnreadFor(player, entry, unread);
    }
}
