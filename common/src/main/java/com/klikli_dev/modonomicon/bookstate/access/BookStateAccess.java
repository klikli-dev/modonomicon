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
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public interface BookStateAccess {

    boolean isEntryRead(Player player, BookEntry entry);

    boolean isCategoryRead(Player player, BookCategory category);

    boolean canRun(Player player, BookCommand command);

    void setRun(Player player, BookCommand command);

    boolean markEntryRead(ServerPlayer player, BookEntry entry);

    boolean markCategoryRead(ServerPlayer player, BookCategory category);

    void updateAndSync(ServerPlayer player);

    void sync(ServerPlayer player);

    Map<Identifier, Long> getUnlockTimestamps(Player player, Book book);

    BookVisualState getBookVisualState(Player player, Book book);

    CategoryVisualState getCategoryVisualState(Player player, BookCategory category);

    EntryVisualState getEntryVisualState(Player player, BookEntry entry);

    boolean isEntryUnread(Player player, BookEntry entry);

    boolean isCategoryUnread(Player player, BookCategory category);

    void setCategoryUnread(ServerPlayer player, BookCategory category, boolean unread);

    void setEntryVisualState(ServerPlayer player, BookEntry entry, EntryVisualState state);

    void setEntryUnread(ServerPlayer player, BookEntry entry, boolean unread);
}
