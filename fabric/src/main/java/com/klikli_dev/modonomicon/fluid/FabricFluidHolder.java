/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public class FabricFluidHolder implements FluidHolder {

    private final FluidVariant fluidVariant;
    private int amount;

    public FabricFluidHolder(FluidHolder fluid) {
        this(fluid.getFluid().value(), fluid.getAmount(), fluid.getComponents());
    }

    public FabricFluidHolder(Fluid fluid, int amount, DataComponentPatch components) {
        this(FluidVariant.of(fluid, components), amount);
    }

    public FabricFluidHolder(FluidVariant fluidVariant, int amount) {
        this.fluidVariant = fluidVariant;
        this.amount = amount;
    }

    public static FabricFluidHolder empty() {
        return new FabricFluidHolder(FluidVariant.blank(), 0);
    }

    public FluidVariant toVariant() {
        return FluidVariant.of(this.getFluid().value(), this.getComponents());
    }

    @Override
    public Holder<Fluid> getFluid() {
        return this.fluidVariant.getRegistryEntry();
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
    public DataComponentPatch getComponents() {
        return this.fluidVariant.getComponents();
    }


    @Override
    public FluidHolder copy() {
        return new FabricFluidHolder(this.getFluid().value(), this.getAmount(), this.getComponents());
    }
}
