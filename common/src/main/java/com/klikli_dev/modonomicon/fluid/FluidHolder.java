/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public interface FluidHolder {

    int BUCKET_VOLUME = 1000;

    Fluid getFluid();

    boolean isEmpty();

    int getAmount();

    void setAmount(int amount);

    boolean hasTag();

    CompoundTag getTag();

    void setTag(CompoundTag tag);

    FluidHolder copy();
}
