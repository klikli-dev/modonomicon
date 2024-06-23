// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.mojang.datafixers.util.Pair;

public abstract class IndexModeEntryProvider extends EntryProvider{

    public IndexModeEntryProvider(CategoryProviderBase parent) {
        super(parent);
    }

    /**
     * Implement this and return the U/V coordinates of the entry background. See also @link{BookEntryModel#withEntryBackground(int, int)}
     */
    protected Pair<Integer, Integer> entryBackground(){
        //index mode rendering does not use this
        return EntryBackground.DEFAULT;
    }
}
