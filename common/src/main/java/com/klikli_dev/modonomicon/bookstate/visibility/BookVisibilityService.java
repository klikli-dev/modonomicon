/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visibility;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface BookVisibilityService {

    boolean isVisible(Player player, BookCategory category);

    boolean isVisible(Player player, BookEntry entry);

    boolean isVisible(Player player, BookPage page);

    boolean isAccessible(Player player, BookEntry entry);

    boolean isAccessible(Player player, BookPage page);

    List<BookPage> getVisiblePages(Player player, BookEntry entry);

    EntryDisplayState getEntryDisplayState(Player player, BookEntry entry);
}
