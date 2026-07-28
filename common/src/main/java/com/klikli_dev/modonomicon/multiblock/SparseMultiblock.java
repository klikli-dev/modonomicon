/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.data.MultiblockType;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.registry.MultiblockTypeRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SparseMultiblock extends AbstractMultiblock {

    static final Codec<Map<String, List<BlockPos>>> PATTERN_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(BLOCK_POS_CODEC));

    public static final Identifier ID = Modonomicon.loc("sparse");
    private static final Codec<Map<String, StateMatcher>> SPARSE_MAPPING_CODEC = Codec.unboundedMap(Codec.STRING, StateMatcher.CODEC);
    public static final MapCodec<SparseMultiblock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PATTERN_CODEC.fieldOf("pattern").forGetter(SparseMultiblock::serializedPattern),
            SPARSE_MAPPING_CODEC.fieldOf("mapping").forGetter(SparseMultiblock::serializedMapping)
    ).apply(instance, (pattern, mapping) -> new SparseMultiblock(deserializeStateMatchers(pattern, mapping))));
    public static final StreamCodec<RegistryFriendlyByteBuf, SparseMultiblock> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    final Map<BlockPos, StateMatcher> stateMatchers;
    private final Vec3i size;

    public SparseMultiblock(Map<BlockPos, StateMatcher> stateMatchers) {
        Preconditions.checkArgument(!stateMatchers.isEmpty(), "No data given to sparse multiblock!");

        //this differs from dense multiblock, where we keep a copy of the originally loaded data for serialization, but it should be fine as we have less entries.
        this.stateMatchers = ImmutableMap.copyOf(stateMatchers);
        this.size = this.calculateSize();
    }

    private record SerializedMatcherData(Map<String, List<BlockPos>> pattern, Map<String, StateMatcher> mapping) {}

    static Map<BlockPos, StateMatcher> deserializeStateMatchers(Map<String, List<BlockPos>> pattern, Map<String, StateMatcher> mapping) {
        Map<BlockPos, StateMatcher> stateMatchers = new Object2ObjectOpenHashMap<>();
        for (var entry : pattern.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new IllegalArgumentException("Pattern key needs to be only 1 character: " + entry.getKey());
            }
            var matcher = mapping.get(entry.getKey());
            if (matcher == null) {
                throw new IllegalArgumentException("Missing matcher mapping for key " + entry.getKey());
            }
            for (var pos : entry.getValue()) {
                stateMatchers.put(pos, matcher);
            }
        }
        return stateMatchers;
    }

    private SerializedMatcherData serializeMatcherData() {
        Map<StateMatcher, List<BlockPos>> grouped = new LinkedHashMap<>();
        this.stateMatchers.entrySet().stream()
                .sorted((left, right) -> {
                    int compareX = Integer.compare(left.getKey().getX(), right.getKey().getX());
                    if (compareX != 0) return compareX;
                    int compareY = Integer.compare(left.getKey().getY(), right.getKey().getY());
                    if (compareY != 0) return compareY;
                    return Integer.compare(left.getKey().getZ(), right.getKey().getZ());
                })
                .forEach(entry -> grouped.computeIfAbsent(entry.getValue(), ignored -> new ArrayList<>()).add(entry.getKey()));

        Map<String, List<BlockPos>> pattern = new LinkedHashMap<>();
        Map<String, StateMatcher> mapping = new LinkedHashMap<>();
        int index = 0;
        for (var entry : grouped.entrySet()) {
            char key = (char) (33 + index++);
            String stringKey = String.valueOf(key);
            pattern.put(stringKey, entry.getValue());
            mapping.put(stringKey, entry.getKey());
        }
        return new SerializedMatcherData(pattern, mapping);
    }

    Map<String, List<BlockPos>> serializedPattern() {
        return this.serializeMatcherData().pattern();
    }

    Map<String, StateMatcher> serializedMapping() {
        return this.serializeMatcherData().mapping();
    }

    private Vec3i calculateSize() {
        int minX = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getX).min().getAsInt();
        int maxX = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getX).max().getAsInt();
        int minY = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getY).min().getAsInt();
        int maxY = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getY).max().getAsInt();
        int minZ = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getZ).min().getAsInt();
        int maxZ = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getZ).max().getAsInt();
        return new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }

    @Override
    public Vec3i getSize() {
        return this.size;
    }

    @Override
    public MultiblockType<?> type() {
        return MultiblockTypeRegistry.SPARSE;
    }

    @Override
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset) {
        BlockPos disp = forView
                ? new BlockPos(-this.viewOffX, -this.viewOffY + 1, -this.viewOffZ).rotate(rotation)
                : new BlockPos(-this.offX, -this.offY, -this.offZ).rotate(rotation);
        if (disableOffset)
            disp = BlockPos.ZERO;

        // the local origin of this multiblock, in world coordinates
        BlockPos origin = anchor.offset(disp);
        List<SimulateResult> ret = new ArrayList<>();
        for (var e : this.stateMatchers.entrySet()) {
            BlockPos currDisp = e.getKey().rotate(rotation);
            BlockPos actionPos = origin.offset(currDisp);
            ret.add(new SimulateResultImpl(actionPos, e.getValue(), null));
        }
        return Pair.of(origin, ret);
    }

    @Override
    public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
        this.setLevel(world);
        BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(rotation));
        BlockState state = world.getBlockState(checkPos).rotate(AbstractMultiblock.fixHorizontal(rotation));
        StateMatcher matcher = this.stateMatchers.getOrDefault(new BlockPos(x, y, z), Matchers.ANY);
        return matcher.getStatePredicate().test(world, checkPos, state);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        long ticks = this.level != null ? this.level.getGameTime() : 0L;
        return this.stateMatchers.getOrDefault(pos, Matchers.AIR).getDisplayedState(ticks);
    }
}
