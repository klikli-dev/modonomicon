/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.api.stub.multiblock.matcher;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class StubMatcher implements StateMatcher {

    public static final ResourceLocation TYPE = ResourceLocation.parse(ModonomiconAPI.ID + ":stub");

    public static final StubMatcher INSTANCE = new StubMatcher();

    private final BlockState state = Blocks.AIR.defaultBlockState();

    private StubMatcher() {
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.state;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return (w, p, s) -> false;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {

    }

    @Override
    public boolean countsTowardsTotalBlocks() {
        return true;
    }
}
