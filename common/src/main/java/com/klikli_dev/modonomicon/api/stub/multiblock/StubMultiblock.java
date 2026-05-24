/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.api.stub.multiblock;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.MultiblockType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class StubMultiblock implements Multiblock {

    public static final Identifier TYPE = Identifier.parse(ModonomiconAPI.ID + ":stub");

    public static final StubMultiblock INSTANCE = new StubMultiblock();
    private static final MultiblockType<StubMultiblock> MULTIBLOCK_TYPE = new MultiblockType<>(
            TYPE,
            com.mojang.serialization.MapCodec.unit(INSTANCE),
            StreamCodec.unit(INSTANCE)
    );

    private StubMultiblock() {
    }

    @Override
    public MultiblockType<?> type() {
        return MULTIBLOCK_TYPE;
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
    public Multiblock setId(Identifier res) {
        return this;
    }

    @Override
    public boolean isSymmetrical() {
        return false;
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, "stub");
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
        Multiblock.toNetwork(this, (RegistryFriendlyByteBuf) buffer);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }
}
