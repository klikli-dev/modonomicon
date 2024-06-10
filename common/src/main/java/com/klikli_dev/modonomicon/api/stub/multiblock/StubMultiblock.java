/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.api.stub.multiblock;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class StubMultiblock implements Multiblock {

    public static final ResourceLocation TYPE = ResourceLocation.parse(ModonomiconAPI.ID + ":stub");

    public static final StubMultiblock INSTANCE = new StubMultiblock();

    private StubMultiblock() {
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public void setLevel(Level level) {

    }

    @Override
    public Multiblock offset(int x, int y, int z) {
        return this;
    }

    @Override
    public Multiblock offsetView(int x, int y, int z) {
        return this;
    }

    @Override
    public Multiblock setSymmetrical(boolean symmetrical) {
        return this;
    }

    @Override
    public Multiblock setId(ResourceLocation res) {
        return this;
    }

    @Override
    public boolean isSymmetrical() {
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ModonomiconAPI.ID, "stub");
    }

    @Override
    public void place(Level world, BlockPos pos, Rotation rotation) {
        // NO-OP
    }

    @Override
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset) {
        return Pair.of(BlockPos.ZERO, Collections.emptyList());
    }

    @Override
    public Rotation validate(Level world, BlockPos pos) {
        return null;
    }

    @Override
    public boolean validate(Level world, BlockPos pos, Rotation rotation) {
        return false;
    }

    @Override
    public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
        return false;
    }

    @Override
    public Vec3i getSize() {
        return Vec3i.ZERO;
    }

    @Override
    public Vec3i getOffset() {
        return Vec3i.ZERO;
    }

    @Override
    public Vec3i getViewOffset() {
        return Vec3i.ZERO;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {

    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }
}
