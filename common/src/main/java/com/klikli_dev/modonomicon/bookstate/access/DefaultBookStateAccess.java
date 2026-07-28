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
import com.klikli_dev.modonomicon.bookstate.BookStatesSaveData;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class DefaultBookStateAccess implements BookStateAccess {

    private BookStatesSaveData saveData(Player player) {
        return BookVisualStateManager.get().getSaveDataFor(player);
    }

    @Override
    public boolean isEntryRead(Player player, BookEntry entry) {
        return this.saveData(player).isEntryRead(player.getUUID(), entry);
    }

    @Override
    public boolean isCategoryRead(Player player, BookCategory category) {
        return this.saveData(player).isCategoryRead(player.getUUID(), category);
    }

    public boolean canRun(Player player, BookCommand command) {
        return this.saveData(player).canRunCommand(player.getUUID(), command);
    }

    @Override
    public void setRun(Player player, BookCommand command) {
        this.saveData(player).incrementCommandUses(player.getUUID(), command);
    }

    @Override
    public boolean markEntryRead(ServerPlayer player, BookEntry entry) {
        return this.saveData(player).markEntryRead(player.getUUID(), entry);
    }

    @Override
    public boolean markCategoryRead(ServerPlayer player, BookCategory category) {
        return this.saveData(player).markCategoryRead(player.getUUID(), category);
    }

    @Override
    public void sync(ServerPlayer player) {
        BookVisualStateManager.get().syncFor(player);
    }

    @Override
    public void updateAndSync(ServerPlayer player) {
        this.sync(player);
    }

    @Override
    public Map<Identifier, Long> getUnlockTimestamps(Player player, Book book) {
        return this.saveData(player).getUnlockTimestamps(player.getUUID(), book);
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
