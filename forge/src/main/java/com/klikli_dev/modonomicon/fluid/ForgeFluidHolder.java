/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ForgeFluidHolder implements FluidHolder {
    protected FluidStack fluidStack;

    public ForgeFluidHolder(FluidStack stack) {
        this.fluidStack = stack.copy();
    }

    public ForgeFluidHolder(FluidHolder fluid) {
        this(fluid.getFluid(), fluid.getAmount(), fluid.getComponents());
    }

    public ForgeFluidHolder(Fluid fluid, int amount, CompoundTag tag) {
        this.fluidStack = new FluidStack(fluid, amount, tag);
    }

    public ForgeFluidHolder(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this.fluidStack = new FluidStack(fluid.value(), amount, (CompoundTag) DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, patch).getOrThrow());
    }


    public static FluidStack toStack(FluidHolder fluidHolder) {
        return new FluidStack(fluidHolder.getFluid().value(), fluidHolder.getAmount(), (CompoundTag) DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, fluidHolder.getComponents()).getOrThrow());
    }

    public static ForgeFluidHolder empty() {
        return new ForgeFluidHolder(FluidStack.EMPTY);
    }

    @Override
    public Holder<Fluid> getFluid() {
        return BuiltInRegistries.FLUID.wrapAsHolder(this.fluidStack.getFluid());
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
        return DataComponentPatch.CODEC.decode(NbtOps.INSTANCE, this.fluidStack.getTag()).getOrThrow().getFirst();
    }

    @Override
    public FluidHolder copy() {
        return new ForgeFluidHolder(this.getFluid().value(), this.getAmount(), this.fluidStack.getTag());
    }

    public FluidStack toStack() {
        return new FluidStack(this.getFluid().value(), this.getAmount(), this.fluidStack.getTag());
    }
}
