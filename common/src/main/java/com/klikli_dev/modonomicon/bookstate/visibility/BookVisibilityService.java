/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate.visibility;

import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.world.entity.player.Player;

public interface BookVisibilityService {

    boolean isUnlocked(Player player, BookEntry entry);

    EntryDisplayState getEntryDisplayState(Player player, BookEntry entry);

    boolean isVisible(Player player, BookEntry entry);
}
