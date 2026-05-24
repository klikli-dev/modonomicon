/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.api.multiblock;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A composite element of a rendering block state, and a predicate to validate if the real state in the world is valid
 * or not. Used as the core building block for multiblocks.
 */
public interface StateMatcher {

    Codec<BlockState> BLOCK_STATE_CODEC = Codec.STRING.comapFlatMap(input -> {
        try {
            return DataResult.success(parseBlockState(input));
        } catch (IllegalArgumentException e) {
            return DataResult.error(e::getMessage);
        }
    }, StateMatcher::serializeBlockState);

    Codec<Block> BLOCK_CODEC = Identifier.CODEC.comapFlatMap(id -> BuiltInRegistries.BLOCK.containsKey(id)
            ? DataResult.success(BuiltInRegistries.BLOCK.getValue(id))
            : DataResult.error(() -> "Unknown block " + id), BuiltInRegistries.BLOCK::getKey);

    Codec<TagKey<Block>> BLOCK_TAG_CODEC = Identifier.CODEC.xmap(id -> TagKey.create(Registries.BLOCK, id), TagKey::location);

    Codec<StateMatcher> CODEC = Codec.lazyInitialized(() -> StateMatcherTypeRegistry.codec().dispatch(
            "type",
            StateMatcher::type,
            type -> (MapCodec<? extends StateMatcher>) type.codec()
    ));

    @SuppressWarnings("unchecked")
    StreamCodec<RegistryFriendlyByteBuf, StateMatcher> STREAM_CODEC = StreamCodec.recursive(codec ->
            (StreamCodec<RegistryFriendlyByteBuf, StateMatcher>) (StreamCodec<?, ?>) StateMatcherTypeRegistry.streamCodec()
                    .dispatch(
                            StateMatcher::type,
                            type -> (StreamCodec<? super RegistryFriendlyByteBuf, ? extends StateMatcher>) type.streamCodec()
                    ));

    static BlockState parseBlockState(String input) {
        try {
            return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, new StringReader(input), false).blockState();
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse block state '" + input + "'", e);
        }
    }

    static String serializeBlockState(BlockState state) {
        return BlockStateParser.serialize(state);
    }

    static StateMatcher fromJson(JsonObject json, HolderLookup.Provider provider) {
        return CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode state matcher: " + error));
    }

    static StateMatcher fromNetwork(RegistryFriendlyByteBuf buffer) {
        return STREAM_CODEC.decode(buffer);
    }

    static void toNetwork(StateMatcher matcher, RegistryFriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, matcher);
    }

    StateMatcherType<?> type();

    /**
     * Gets the state displayed by this state matcher for rendering the multiblock page type and the in-world preview.
     *
     * @param ticks World ticks, to allow cycling the state shown.
     */
    BlockState getDisplayedState(long ticks);

    /**
     * Returns a predicate that validates whether the given state is acceptable. This should check the passed in
     * blockstate instead of requerying it from the world, for both performance and correctness reasons -- the state may
     * be rotated for multiblock matching.
     */
    TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate();

    /**
     * Serializes the state matcher to the given buffer.
     */
    default void toNetwork(FriendlyByteBuf buffer) {
        StateMatcher.toNetwork(this, (RegistryFriendlyByteBuf) buffer);
    }

    /**
     * If true this state matcher counts towards the total blocks in the multiblock preview display.
     * If false it behaves like air.
     */
    boolean countsTowardsTotalBlocks();

}
