// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.client.gui.book.theme.GuiTexture;

public abstract class IndexModeEntryProvider extends EntryProvider{

    public IndexModeEntryProvider(CategoryProviderBase parent) {
        super(parent);
    }

    /**
     * Implement this and return the GUI texture used for the entry background.
     */
    protected GuiTexture entryBackground(){
        //index mode rendering does not use this
        return EntryBackground.DEFAULT;
    }
}
