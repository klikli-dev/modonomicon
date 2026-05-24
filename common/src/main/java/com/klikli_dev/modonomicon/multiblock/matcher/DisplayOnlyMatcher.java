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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;


/**
 * Matches any block, including air, but displays a block in the multiblock preview.
 */
public class DisplayOnlyMatcher implements StateMatcher {
    public static final Identifier ID = Modonomicon.loc("display");
    static final Codec<BlockState> DISPLAY_STATE_CODEC = Codec.STRING.comapFlatMap(input -> {
        try {
            return DataResult.success(StateMatcher.parseBlockState(input));
        } catch (IllegalArgumentException e) {
            return DataResult.error(e::getMessage);
        }
    }, StateMatcher::serializeBlockState);
    public static final MapCodec<DisplayOnlyMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(matcher -> Optional.ofNullable(matcher.displayState))
    ).apply(instance, optional -> new DisplayOnlyMatcher(optional.orElse(null))));
    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayOnlyMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final BlockState displayState;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public DisplayOnlyMatcher(BlockState displayState) {
        this.displayState = displayState;
        this.predicate = (blockGetter, blockPos, blockState) -> true;
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.DISPLAY;
    }

    public BlockState displayState() {
        return this.displayState;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState;
    }

    @Override
    public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
        return this.predicate;
    }

    @Override
    public boolean countsTowardsTotalBlocks() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (DisplayOnlyMatcher) o;
        return this.displayState.equals(that.displayState);
    }
}
