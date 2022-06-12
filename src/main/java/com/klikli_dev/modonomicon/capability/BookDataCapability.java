/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.capability;

import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BookDataCapability implements INBTSerializable<CompoundTag> {


    /**
     * Clones the settings from an existing settings instance into this instance
     *
     * @param capability the existing settings instance.
     */
    public void clone(BookDataCapability capability) {
        //TODO: clone contents
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        //TODO: save to NBT
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //TODO: load from NBT
    }

    public static class Dispatcher implements ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<BookDataCapability> bookDataCapability = LazyOptional.of(
                BookDataCapability::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityRegistry.BOOK_DATA) {
                return this.bookDataCapability.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.bookDataCapability.map(BookDataCapability::serializeNBT).orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bookDataCapability.ifPresent(capability -> capability.deserializeNBT(nbt));
        }
    }
}
