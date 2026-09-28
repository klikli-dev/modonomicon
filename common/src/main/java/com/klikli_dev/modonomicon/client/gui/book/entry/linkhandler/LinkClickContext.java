// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The context a {@link LinkHandler} operates in.
 * <p>
 * Entry pages provide the viewed entry (and category), while other link-bearing texts, such as category or book
 * descriptions, provide no entry. Handlers must work with a {@code null} entry id.
 */
public interface LinkClickContext {

    Book book();

    /**
     * The entry the clicked text belongs to, or {@code null} if the text is not rendered as part of an entry
     * (e.g. a category or book description).
     */
    @Nullable
    Identifier entryId();

    /**
     * The category the clicked text belongs to, or {@code null} if there is none.
     */
    @Nullable
    Identifier categoryId();

    default Player player() {
        return Minecraft.getInstance().player;
    }

    /**
     * Pushes the currently viewed location to the back-history, so a link target can navigate back to it.
     * Contexts without a navigable location (e.g. descriptions) leave this as a no-op.
     */
    default void pushCurrentToHistory() {
    }

    /**
     * Closes the current screen stack, preserving state, e.g. before opening an external (patchouli) book.
     */
    void closeForExternalNavigation();
}
