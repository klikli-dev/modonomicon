// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.networking.SyncBookUnlockStatesMessage;

/**
 * A screen that represents a book. It usually manages other screens for categories and entries.
 */
public interface BookParentScreen {

    Book getBook();

    void onDisplay();

    /**
     * This is provided by any vanilla screen, and usually overridden by Modonomicon screens.
     * Making it available in this interface allows various book child screens to close the entire book.
     */
    void onClose();

    void loadState(BookVisualState state);

    void saveState(BookVisualState state);

    void onSyncBookUnlockStatesMessage(SyncBookUnlockStatesMessage message);
}
