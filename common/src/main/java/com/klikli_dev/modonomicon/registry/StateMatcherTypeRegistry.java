/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.multiblock.matcher.AnyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.BlockMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.BlockStateMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.BlockStatePropertyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.DisplayOnlyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.PredicateMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.TagMatcher;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class StateMatcherTypeRegistry {

    private static final DispatchCodecRegistry<StateMatcherType<?>> TYPES = new DispatchCodecRegistry<>(StateMatcherType::id, "state matcher type");

    public static final StateMatcherType<AnyMatcher> ANY = register(AnyMatcher.ID, AnyMatcher.CODEC, AnyMatcher.STREAM_CODEC);

    public static final StateMatcherType<BlockMatcher> BLOCK = register(BlockMatcher.ID, BlockMatcher.CODEC, BlockMatcher.STREAM_CODEC);

    public static final StateMatcherType<BlockStateMatcher> BLOCK_STATE = register(BlockStateMatcher.ID, BlockStateMatcher.CODEC, BlockStateMatcher.STREAM_CODEC);

    public static final StateMatcherType<BlockStatePropertyMatcher> BLOCK_STATE_PROPERTY = register(BlockStatePropertyMatcher.ID, BlockStatePropertyMatcher.CODEC, BlockStatePropertyMatcher.STREAM_CODEC);

    public static final StateMatcherType<DisplayOnlyMatcher> DISPLAY = register(DisplayOnlyMatcher.ID, DisplayOnlyMatcher.CODEC, DisplayOnlyMatcher.STREAM_CODEC);

    public static final StateMatcherType<PredicateMatcher> PREDICATE = register(PredicateMatcher.ID, PredicateMatcher.CODEC, PredicateMatcher.STREAM_CODEC);

    public static final StateMatcherType<TagMatcher> TAG = register(TagMatcher.ID, TagMatcher.CODEC, TagMatcher.STREAM_CODEC);

    private StateMatcherTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends com.klikli_dev.modonomicon.api.multiblock.StateMatcher> StateMatcherType<T> register(Identifier id, com.mojang.serialization.MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, new StateMatcherType<>(id, codec, streamCodec));
    }

    public static com.mojang.serialization.Codec<StateMatcherType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, StateMatcherType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
