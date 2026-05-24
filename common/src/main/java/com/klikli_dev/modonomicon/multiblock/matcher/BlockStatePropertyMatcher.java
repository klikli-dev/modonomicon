/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.google.common.base.Suppliers;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Matches a BlockState, respecting only the provided BlockState properties.
 */
public class BlockStatePropertyMatcher implements StateMatcher {
    public static final Identifier ID = Modonomicon.loc("blockstateproperty");
    public static final MapCodec<BlockStatePropertyMatcher> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DisplayOnlyMatcher.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(m -> Optional.ofNullable(m.displayState)),
            Codec.STRING.fieldOf("block").forGetter(BlockStatePropertyMatcher::blockString)
    ).apply(instance, (displayState, blockString) -> fromEncoded(displayState.orElse(null), blockString)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStatePropertyMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final BlockState displayState;
    private final Block block;

    private final Supplier<Map<String, String>> props;
    private final TriPredicate<BlockGetter, BlockPos, BlockState> predicate;

    public BlockStatePropertyMatcher(BlockState displayState, Block block, Supplier<Map<String, String>> props) {
        this.displayState = displayState;
        this.block = block;
        this.props = props;
        this.predicate = (blockGetter, blockPos, blockState) -> blockState.getBlock() == block && TagMatcher.checkProps(blockState, this.props);
    }

    public static BlockStatePropertyMatcher fromEncoded(BlockState displayState, String blockString) {
        try {
            var result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, new StringReader(blockString), false);
            var props = convertProps(result.properties());
            return new BlockStatePropertyMatcher(displayState, result.blockState().getBlock(), Suppliers.memoize(() -> props));
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse BlockState from value '" + blockString + "' for BlockStatePropertyMatcher.", e);
        }
    }

    private static Map<String, String> convertProps(Map<Property<?>, Comparable<?>> props) {
        Map<String, String> newProps = new Object2ObjectOpenHashMap<>();
        for (var entry : props.entrySet()) {

            appendProperty(newProps, entry.getKey(), entry.getValue());
        }
        return newProps;
    }

    private static <T extends Comparable<T>> void appendProperty(Map<String, String> properties, Property<T> pProperty, Comparable<?> pValue) {
        properties.put(pProperty.getName(), pProperty.getName((T) pValue));
    }

    public String blockString() {
        StringBuilder builder = new StringBuilder(BuiltInRegistries.BLOCK.getKey(this.block).toString());
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
        return StateMatcherTypeRegistry.BLOCK_STATE_PROPERTY;
    }

    public BlockState displayState() {
        return this.displayState;
    }

    public Block block() {
        return this.block;
    }

    public Supplier<Map<String, String>> props() {
        return this.props;
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
        return Objects.hash(this.block, this.displayState, this.props);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BlockStatePropertyMatcher) o;
        return this.block.equals(that.block) && this.props.equals(that.props) && this.displayState.equals(that.displayState);
    }
}
