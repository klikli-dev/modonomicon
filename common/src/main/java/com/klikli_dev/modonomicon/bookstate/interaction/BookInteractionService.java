/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.interaction;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface BookInteractionService {

    boolean markEntryRead(ServerPlayer player, BookEntry entry);

    boolean markCategoryRead(ServerPlayer player, BookCategory category);

    boolean isEntryRead(Player player, BookEntry entry);

    boolean isCategoryRead(Player player, BookCategory category);

    boolean isCategoryUnread(Player player, BookCategory category);

    boolean isEntryUnread(Player player, BookEntry entry);
}
