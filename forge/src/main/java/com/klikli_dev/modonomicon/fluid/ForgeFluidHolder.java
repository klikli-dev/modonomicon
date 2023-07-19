/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ForgeFluidHolder implements FluidHolder {
    protected FluidStack fluidStack;

    public ForgeFluidHolder(FluidStack stack) {
        this.fluidStack = stack.copy();
    }

    public ForgeFluidHolder(FluidHolder fluid) {
        this(fluid.getFluid(), fluid.getAmount(), fluid.getTag());
    }

    public ForgeFluidHolder(Fluid fluid, int amount, CompoundTag tag) {
        this.fluidStack = new FluidStack(fluid, amount, tag);
    }

    public static FluidStack toStack(FluidHolder fluidHolder) {
        return new FluidStack(fluidHolder.getFluid(), fluidHolder.getAmount(), fluidHolder.getTag());
    }

    public static ForgeFluidHolder empty() {
        return new ForgeFluidHolder(FluidStack.EMPTY);
    }

    @Override
    public Fluid getFluid() {
        return this.fluidStack.getFluid();
    }

    @Override
    public boolean isEmpty() {
        return this.fluidStack.isEmpty();
    }

    @Override
    public int getAmount() {
        return this.fluidStack.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        this.fluidStack.setAmount(amount);
    }

    @Override
    public boolean hasTag() {
        return this.fluidStack.hasTag();
    }

    @Override
    public CompoundTag getTag() {
        return this.fluidStack.getTag();
    }

    @Override
    public void setTag(CompoundTag tag) {
        this.fluidStack.setTag(tag);
    }

    @Override
    public FluidHolder copy() {
        return new ForgeFluidHolder(this.getFluid(), this.getAmount(), this.getTag());
    }

    public FluidStack toStack() {
        return new FluidStack(this.getFluid(), this.getAmount(), this.getTag());
    }
}
