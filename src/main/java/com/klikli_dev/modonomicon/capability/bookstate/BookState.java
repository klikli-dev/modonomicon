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

public class BookState implements INBTSerializable<CompoundTag> {

    public Map<ResourceLocation, CategoryState> categoryStates = new HashMap<>();

    public ResourceLocation openCategory = null;


    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();

        var categoryStatesList = new ListTag();
        compound.put("category_states", categoryStatesList);
        this.categoryStates.forEach((categoryId, state) -> {
            var stateCompound = new CompoundTag();
            stateCompound.putString("category_id", categoryId.toString());
            stateCompound.put("state", state.serializeNBT());
            categoryStatesList.add(stateCompound);
        });

        if (this.openCategory != null) {
            compound.putString("open_category", this.openCategory.toString());
        }

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.categoryStates.clear();

        var categoryStatesList = nbt.getList("category_states", Tag.TAG_COMPOUND);
        for (var categoryStateEntry : categoryStatesList) {
            if (categoryStateEntry instanceof CompoundTag categoryStateCompound) {
                var bookId = new ResourceLocation(categoryStateCompound.getString("category_id"));
                var stateCompound = categoryStateCompound.getCompound("state");
                var state = new CategoryState();
                state.deserializeNBT(stateCompound);
                this.categoryStates.put(bookId, state);
            }
        }

        if (nbt.contains("open_category")) {
            this.openCategory = new ResourceLocation(nbt.getString("open_category"));
        }
    }
}
