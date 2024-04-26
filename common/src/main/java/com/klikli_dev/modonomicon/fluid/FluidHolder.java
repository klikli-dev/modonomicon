/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public interface FluidHolder {

    int BUCKET_VOLUME = 1000;

    Holder<Fluid> getFluid();

    boolean isEmpty();

    int getAmount();

    void setAmount(int amount);

    public DataComponentPatch getComponents();

    FluidHolder copy();
}
