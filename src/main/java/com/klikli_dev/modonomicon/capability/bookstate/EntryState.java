/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.capability.bookstate;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EntryState implements INBTSerializable<CompoundTag> {
    public int openPagesIndex;

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putInt("openPagesIndex", this.openPagesIndex);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.openPagesIndex = nbt.getInt("openPagesIndex");
    }
}
