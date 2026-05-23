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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

/**
 * Matches a block, ignoring the BlockState properties.
 */
public class BlockMatcher implements StateMatcher {
    public static final Identifier ID = Modonomicon.loc("block");
    public static final MapCodec<BlockMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DisplayOnlyMatcher.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(m -> Optional.ofNullable(m.displayState)),
            StateMatcher.BLOCK_CODEC.fieldOf("block").forGetter(BlockMatcher::block)
    ).apply(instance, (displayState, block) -> new BlockMatcher(displayState.orElse(null), block)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final BlockState displayState;
    private final Block block;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public BlockMatcher(BlockState displayState, Block block) {
        this.displayState = displayState;
        this.block = block;
        this.predicate = (blockGetter, blockPos, blockState) ->
                blockState.getBlock() == block;
    }

    public static BlockMatcher from(Block block) {
        return new BlockMatcher(null, block);
    }

    public static BlockMatcher from(BlockState displayState, Block block) {
        return new BlockMatcher(displayState, block);
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.BLOCK;
    }

    public BlockState displayState() {
        return this.displayState;
    }

    public Block block() {
        return this.block;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState == null ? this.block.defaultBlockState() : this.displayState;
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
        return Objects.hash(this.block, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockMatcher) o;
        return this.block.equals(that.block) && this.displayState.equals(that.displayState);
    }
}
