/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.capability.bookstate;

import net.minecraft.nbt.CompoundTag;

public class EntryState {
    public int openPagesIndex;

    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putInt("openPagesIndex", this.openPagesIndex);
        return compound;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.openPagesIndex = nbt.getInt("openPagesIndex");
    }
}
