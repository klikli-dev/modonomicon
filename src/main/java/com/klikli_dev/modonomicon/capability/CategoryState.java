/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class CategoryState implements INBTSerializable<CompoundTag> {
    public float scrollX = 0;
    public float scrollY = 0;
    public float targetZoom;

    public ResourceLocation openEntry = null;

    //TODO: open pages should be moved into separate entry state
    public int openPagesIndex;

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putFloat("scrollX", this.scrollX);
        tag.putFloat("scrollY", this.scrollY);
        tag.putFloat("targetZoom", this.targetZoom);
        if(this.openEntry != null) {
            tag.putString("openEntry", this.openEntry.toString());
        }
        tag.putInt("openPagesIndex", this.openPagesIndex);
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.scrollX = nbt.getFloat("scrollX");
        this.scrollY = nbt.getFloat("scrollY");
        this.targetZoom = nbt.getFloat("targetZoom");
        this.openPagesIndex = nbt.getInt("openPagesIndex");
        if(nbt.contains("openEntry")) {
            this.openEntry = new ResourceLocation(nbt.getString("openEntry"));
        }
    }
}
