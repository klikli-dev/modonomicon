/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.capability.bookstate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CategoryState implements INBTSerializable<CompoundTag> {

    public ConcurrentMap<ResourceLocation, EntryState> entryStates = new ConcurrentHashMap<>();

    public float scrollX = 0;
    public float scrollY = 0;
    public float targetZoom = 0.7f;

    public ResourceLocation openEntry = null;

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putFloat("scrollX", this.scrollX);
        compound.putFloat("scrollY", this.scrollY);
        compound.putFloat("targetZoom", this.targetZoom);
        if(this.openEntry != null) {
            compound.putString("openEntry", this.openEntry.toString());
        }

        var entryStatesList = new ListTag();
        compound.put("entry_states", entryStatesList);
        this.entryStates.forEach((entryId, state) -> {
            var stateCompound = new CompoundTag();
            stateCompound.putString("entry_id", entryId.toString());
            stateCompound.put("state", state.serializeNBT());
            entryStatesList.add(stateCompound);
        });

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.scrollX = nbt.getFloat("scrollX");
        this.scrollY = nbt.getFloat("scrollY");
        this.targetZoom = nbt.getFloat("targetZoom");
        if(nbt.contains("openEntry")) {
            this.openEntry = new ResourceLocation(nbt.getString("openEntry"));
        }

        var entryStatesList = nbt.getList("entry_states", Tag.TAG_COMPOUND);
        for (var entryStateEntry : entryStatesList) {
            if (entryStateEntry instanceof CompoundTag entryStateCompound) {
                var bookId = new ResourceLocation(entryStateCompound.getString("entry_id"));
                var stateCompound = entryStateCompound.getCompound("state");
                var state = new EntryState();
                state.deserializeNBT(stateCompound);
                this.entryStates.put(bookId, state);
            }
        }

    }
}
