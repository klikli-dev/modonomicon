/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * Matches against the predicate with the given id. Predicates are stored in {@link
 * LoaderRegistry}
 */
public class PredicateMatcher implements StateMatcher {

    public static final ResourceLocation TYPE = Modonomicon.loc("predicate");

    private final BlockState displayState;
    private final ResourceLocation predicateId;
    private final Supplier<TriPredicate<BlockGetter, BlockPos, BlockState>> predicate;

    private final boolean countsTowardsTotalBlocks;

    protected PredicateMatcher(BlockState displayState, ResourceLocation predicateId, boolean countsTowardsTotalBlocks) {
        this.displayState = displayState;
        this.predicateId = predicateId;
        this.predicate = Suppliers.memoize(() -> LoaderRegistry.getPredicate(this.predicateId));
        this.countsTowardsTotalBlocks = countsTowardsTotalBlocks;
    }

    public static PredicateMatcher fromJson(JsonObject json, HolderLookup.Provider provider) {
        try {
            var displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(GsonHelper.getAsString(json, "display")), false).blockState();
            var predicateId = ResourceLocation.parse(GsonHelper.getAsString(json, "predicate"));
            var countsTowardsTotalBlocks = GsonHelper.getAsBoolean(json, "counts_towards_total_blocks", true);
            return new PredicateMatcher(displayState, predicateId, countsTowardsTotalBlocks);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from json member \"display\" for PredicateMatcher.", e);
        }
    }

    public static PredicateMatcher fromNetwork(FriendlyByteBuf buffer) {
        try {
            var displayState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(buffer.readUtf()), false).blockState();
            var predicateId = buffer.readResourceLocation();
            var countsTowardsTotalBlocks = buffer.readBoolean();
            return new PredicateMatcher(displayState, predicateId, countsTowardsTotalBlocks);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse PredicateMatcher from network.", e);
        }
    }

    public ResourceLocation getPredicateId() {
        return this.predicateId;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
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
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(BlockStateParser.serialize(this.displayState));
        buffer.writeUtf(this.predicateId.toString());
        buffer.writeBoolean(this.countsTowardsTotalBlocks);
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
