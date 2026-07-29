/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.api.stub.multiblock.matcher;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class StubMatcher implements StateMatcher {

    public static final Identifier TYPE = Identifier.parse(ModonomiconAPI.ID + ":stub");

    public static final StubMatcher INSTANCE = new StubMatcher();
    private static final StateMatcherType<StubMatcher> MATCHER_TYPE = new StateMatcherType<>(
            TYPE,
            com.mojang.serialization.MapCodec.unit(INSTANCE),
            StreamCodec.unit(INSTANCE)
    );

    private final BlockState state = Blocks.AIR.defaultBlockState();

    private StubMatcher() {
    }

    @Override
    public StateMatcherType<?> type() {
        return MATCHER_TYPE;
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
        StateMatcher.toNetwork(this, (RegistryFriendlyByteBuf) buffer);
    }

    @Override
    public boolean countsTowardsTotalBlocks() {
        return true;
    }
}
