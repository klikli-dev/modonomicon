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
import net.neoforged.neoforge.fluids.FluidStack;

public class NeoFluidHolder implements FluidHolder {
    protected FluidStack fluidStack;

    public NeoFluidHolder(FluidStack stack) {
        this.fluidStack = stack.copy();
    }

    public NeoFluidHolder(FluidHolder fluid) {
        this(fluid.getFluid(), fluid.getAmount(), fluid.getComponents());
    }

    public NeoFluidHolder(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this.fluidStack = new FluidStack(fluid, amount, patch);
    }

    public static FluidStack toStack(FluidHolder fluidHolder) {
        return new FluidStack(fluidHolder.getFluid(), fluidHolder.getAmount(), fluidHolder.getComponents());
    }

    public static NeoFluidHolder empty() {
        return new NeoFluidHolder(FluidStack.EMPTY);
    }

    @Override
    public Holder<Fluid> getFluid() {
        return this.fluidStack.getFluidHolder();
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
    public DataComponentPatch getComponents() {
        return this.fluidStack.getComponentsPatch();
    }

    @Override
    public FluidHolder copy() {
        return new NeoFluidHolder(this.getFluid(), this.getAmount(), this.getComponents());
    }

    public FluidStack toStack() {
        return new FluidStack(this.getFluid(), this.getAmount(), this.getComponents());
    }
}
