/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Matches against the given tag, and optionally checks for the given BlockState properties.
 */
public class TagMatcher implements StateMatcher {
    public static final Identifier ID = Modonomicon.loc("tag");
    public static final MapCodec<TagMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DisplayOnlyMatcher.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(m -> Optional.ofNullable(m.displayState)),
            Codec.STRING.fieldOf("tag").forGetter(TagMatcher::tagString)
    ).apply(instance, (displayState, tagString) -> fromEncoded(displayState.orElse(null), tagString)));
    public static final StreamCodec<RegistryFriendlyByteBuf, TagMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final BlockState displayState;
    private final Supplier<TagKey<Block>> tag;
    private final Supplier<Map<String, String>> props;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    protected TagMatcher(Supplier<TagKey<Block>> tag, Supplier<Map<String, String>> props) {
        this(null, tag, props);
    }

    public TagMatcher(BlockState displayState, Supplier<TagKey<Block>> tag, Supplier<Map<String, String>> props) {
        this.displayState = displayState;
        this.tag = tag;
        this.props = props;
        this.predicate = (blockGetter, blockPos, blockState) -> blockState.is(this.tag.get()) && checkProps(blockState, this.props);
    }

    private static String normalizeTagString(String tagString) {
        if (!tagString.startsWith("#")) {
            tagString = "#" + tagString;
        }

        return tagString;
    }

    public static TagMatcher fromEncoded(BlockState displayState, String tagString) {
        String finalTagString = normalizeTagString(tagString);
        Supplier<TagKey<Block>> tagSupplier = Suppliers.memoize(() -> {
            try {
                var parserResult = BlockStateParser.parseForTesting(BuiltInRegistries.BLOCK, new StringReader(finalTagString), true).right().orElseThrow();
                return parserResult.tag().unwrap().left().orElseThrow();
            } catch (CommandSyntaxException e) {
                Modonomicon.LOG.error("Failed to parse Tag and BlockState properties from json member \"tag\" for TagMatcher: {0}. Will use \"modonomicon:bedrock\" as fallback, Exception: {1}", finalTagString, e);
                return TagKey.create(Registries.BLOCK, Modonomicon.loc("bedrock"));
            }
        });

        Supplier<Map<String, String>> propsSupplier = Suppliers.memoize(() -> {
            try {
                var parserResult = BlockStateParser.parseForTesting(BuiltInRegistries.BLOCK, new StringReader(finalTagString), true).right().orElseThrow();
                return parserResult.vagueProperties();
            } catch (CommandSyntaxException e) {
                Modonomicon.LOG.error("Failed to parse Tag and BlockState properties from json member \"tag\" for TagMatcher: {0}. Will use empty property map as fallback, Exception: {1}", finalTagString, e);
                return Map.of();
            }
        });

        return new TagMatcher(displayState, tagSupplier, propsSupplier);
    }

    public static boolean checkProps(BlockState state, Supplier<Map<String, String>> props) {
        for (Entry<String, String> entry : props.get().entrySet()) {
            Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (prop == null) {
                return false;
            }

            Comparable<?> value = prop.getValue(entry.getValue()).orElse(null);
            if (value == null) {
                return false;
            }

            if (!state.getValue(prop).equals(value)) {
                return false;
            }
        }
        return true;
    }

    public String tagString() {
        StringBuilder builder = new StringBuilder("#").append(this.tag.get().location());
        if (!this.props.get().isEmpty()) {
            builder.append('[');
            boolean first = true;
            for (var entry : this.props.get().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).toList()) {
                if (!first) {
                    builder.append(',');
                }
                builder.append(entry.getKey()).append('=').append(entry.getValue());
                first = false;
            }
            builder.append(']');
        }
        return builder.toString();
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.TAG;
    }

    public BlockState displayState() {
        return this.displayState;
    }

    public Supplier<TagKey<Block>> tag() {
        return this.tag;
    }

    public Supplier<Map<String, String>> props() {
        return this.props;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        if (this.displayState != null) {
            return this.displayState;
        } else {
            var all = ImmutableList.copyOf(BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag.get()));
            if (all.isEmpty()) {
                return Blocks.BEDROCK.defaultBlockState(); // show something impossible
            } else {
                int idx = (int) ((ticks / 20) % all.size());
                return all.get(idx).value().defaultBlockState();
            }
        }
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
        return Objects.hash(this.tag, this.props, this.displayState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (TagMatcher) o;
        return this.tag.equals(that.tag) && this.props.equals(that.props) && this.displayState.equals(that.displayState);
    }
}
