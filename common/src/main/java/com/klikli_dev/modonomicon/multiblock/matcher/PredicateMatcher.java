/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.PredicateRegistry;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import java.util.Objects;

/**
 * Matches against the predicate with the given id. Predicates are stored in {@link
 * PredicateRegistry}
 */
public class PredicateMatcher implements StateMatcher {

    public static final Identifier ID = Modonomicon.loc("predicate");
    public static final MapCodec<PredicateMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DisplayOnlyMatcher.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(m -> Optional.ofNullable(m.displayState)),
            Identifier.CODEC.fieldOf("predicate").forGetter(PredicateMatcher::getPredicateId),
            Codec.BOOL.optionalFieldOf("counts_towards_total_blocks", true).forGetter(PredicateMatcher::countsTowardsTotalBlocks)
    ).apply(instance, (displayState, predicateId, counts) -> new PredicateMatcher(displayState.orElse(null), predicateId, counts)));
    public static final StreamCodec<RegistryFriendlyByteBuf, PredicateMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final BlockState displayState;
    private final Identifier predicateId;
    private final Supplier<TriPredicate<BlockGetter, BlockPos, BlockState>> predicate;

    private final boolean countsTowardsTotalBlocks;

    public PredicateMatcher(BlockState displayState, Identifier predicateId, boolean countsTowardsTotalBlocks) {
        this.displayState = displayState;
        this.predicateId = predicateId;
        this.predicate = Suppliers.memoize(() -> PredicateRegistry.get(this.predicateId));
        this.countsTowardsTotalBlocks = countsTowardsTotalBlocks;
    }

    public Identifier getPredicateId() {
        return this.predicateId;
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.PREDICATE;
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
        return this.predicate.get();
    }

    @Override
    public boolean countsTowardsTotalBlocks() {
        return this.countsTowardsTotalBlocks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.predicateId, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (PredicateMatcher) o;
        return this.predicateId.equals(that.predicateId) && this.displayState.equals(that.displayState);
    }
}
