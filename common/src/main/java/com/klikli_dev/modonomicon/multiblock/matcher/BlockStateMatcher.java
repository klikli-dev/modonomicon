/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

/**
 * Matches a BlockState, respecting all BlockState properties.
 */
public class BlockStateMatcher implements StateMatcher {
    public static final Identifier ID = Modonomicon.loc("blockstate");
    public static final MapCodec<BlockStateMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DisplayOnlyMatcher.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(m -> Optional.ofNullable(m.displayState)),
            StateMatcher.BLOCK_STATE_CODEC.fieldOf("block").forGetter(BlockStateMatcher::blockState)
    ).apply(instance, (displayState, blockState) -> new BlockStateMatcher(displayState.orElse(null), blockState)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final BlockState displayState;
    private final BlockState blockState;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public BlockStateMatcher(BlockState displayState, BlockState blockState) {
        this.displayState = displayState;
        this.blockState = blockState;
        this.predicate = (blockGetter, blockPos, state) -> state == blockState;
    }

    public static BlockStateMatcher from(BlockState blockState) {
        return new BlockStateMatcher(null, blockState);
    }

    public static BlockStateMatcher from(BlockState displayState, BlockState blockState) {
        return new BlockStateMatcher(displayState, blockState);
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.BLOCK_STATE;
    }

    public BlockState displayState() {
        return this.displayState;
    }

    public BlockState blockState() {
        return this.blockState;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState == null ? this.blockState : this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public boolean countsTowardsTotalBlocks() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.blockState, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockStateMatcher) o;
        return this.blockState.equals(that.blockState) && this.displayState.equals(that.displayState);
    }
}
