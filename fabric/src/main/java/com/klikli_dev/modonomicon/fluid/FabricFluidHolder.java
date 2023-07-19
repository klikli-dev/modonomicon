/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public class FabricFluidHolder implements FluidHolder {

    private FluidVariant fluidVariant;
    private int amount;

    public FabricFluidHolder(FluidHolder fluid) {
        this(fluid.getFluid(), fluid.getAmount(), fluid.getTag());
    }

    public FabricFluidHolder(Fluid fluid, int amount, CompoundTag tag) {
        this(FluidVariant.of(fluid, tag), amount);
    }

    public FabricFluidHolder(FluidVariant fluidVariant, int amount) {
        this.fluidVariant = fluidVariant;
        this.amount = amount;
    }

    public static FabricFluidHolder empty() {
        return new FabricFluidHolder(FluidVariant.blank(), 0);
    }

    public FluidVariant toVariant() {
        return FluidVariant.of(this.getFluid(), this.getTag());
    }

    @Override
    public Fluid getFluid() {
        return this.fluidVariant.getFluid();
    }

    @Override
    public boolean isEmpty() {
        return this.amount <= 0;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean hasTag() {
        return this.fluidVariant.hasNbt();
    }

    @Override
    public CompoundTag getTag() {
        return this.fluidVariant.getNbt();
    }

    @Override
    public void setTag(CompoundTag tag) {
        this.fluidVariant = FluidVariant.of(this.fluidVariant.getFluid(), tag);
    }

    @Override
    public FluidHolder copy() {
        return new FabricFluidHolder(this.getFluid(), this.getAmount(), this.getTag());
    }
}
