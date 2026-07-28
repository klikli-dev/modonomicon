/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.data.MultiblockType;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.registry.MultiblockTypeRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DenseMultiblock extends AbstractMultiblock {

    static final Codec<List<List<String>>> PATTERN_CODEC = Codec.list(Codec.list(Codec.STRING));

    public static final Identifier ID = Modonomicon.loc("dense");
    public static final MapCodec<DenseMultiblock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PATTERN_CODEC.fieldOf("pattern").forGetter(DenseMultiblock::patternAsList),
            MAPPING_CODEC.fieldOf("mapping").forGetter(DenseMultiblock::targets)
    ).apply(instance, (pattern, targets) -> new DenseMultiblock(toPatternArray(pattern), targets)));
    public static final StreamCodec<RegistryFriendlyByteBuf, DenseMultiblock> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    final String[][] pattern;
    final Vec3i size;
    /**
     * Keep only for serialization
     */
    final Map<Character, StateMatcher> targets;
    private StateMatcher[][][] stateMatchers;

    public DenseMultiblock(String[][] pattern, Map<Character, StateMatcher> targets) {
        this.pattern = pattern;
        this.targets = targets;

        if (!targets.containsKey('_')) {
            targets.put('_', Matchers.ANY);
        }
        if (!targets.containsKey(' ')) {
            targets.put(' ', Matchers.AIR);
        }
        if (!targets.containsKey('0')) {
            targets.put('0', Matchers.AIR);
        }

        this.size = this.build(targets, getPatternDimensions(pattern));
    }

    static String[][] toPatternArray(List<List<String>> pattern) {
        String[][] array = new String[pattern.size()][];
        for (int i = 0; i < pattern.size(); i++) {
            array[i] = pattern.get(i).toArray(String[]::new);
        }
        return array;
    }

    List<List<String>> patternAsList() {
        List<List<String>> list = new ArrayList<>(this.pattern.length);
        for (var row : this.pattern) {
            list.add(List.of(row));
        }
        return list;
    }

    Map<Character, StateMatcher> targets() {
        return this.targets;
    }

    private static Vec3i getPatternDimensions(String[][] pattern) {
        int expectedLenX = -1;
        int expectedLenZ = -1;
        for (String[] arr : pattern) {
            if (expectedLenX == -1) {
                expectedLenX = arr.length;
            }
            if (arr.length != expectedLenX) {
                throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);
            }

            for (String s : arr) {
                if (expectedLenZ == -1) {
                    expectedLenZ = s.length();
                }
                if (s.length() != expectedLenZ) {
                    throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + s.length());
                }
            }
        }

        return new Vec3i(expectedLenX, pattern.length, expectedLenZ);
    }

    private Vec3i build(Map<Character, StateMatcher> stateMap, Vec3i dimensions) {
        boolean foundCenter = false;

        this.stateMatchers = new StateMatcher[dimensions.getX()][dimensions.getY()][dimensions.getZ()];
        for (int y = 0; y < dimensions.getY(); y++) {
            for (int x = 0; x < dimensions.getX(); x++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    char c = this.pattern[y][x].charAt(z);
                    if (!stateMap.containsKey(c)) {
                        throw new IllegalArgumentException("Character " + c + " isn't mapped");
                    }

                    StateMatcher matcher = stateMap.get(c);
                    if (c == '0') {
                        if (foundCenter) {
                            throw new IllegalArgumentException("A structure can't have two centers");
                        }
                        foundCenter = true;
                        this.offX = x;
                        this.offY = dimensions.getY() - y - 1;
                        this.offZ = z;
                        this.setViewOffset();
                    }

                    this.stateMatchers[x][dimensions.getY() - y - 1][z] = matcher;
                }
            }
        }

        if (!foundCenter) {
            throw new IllegalArgumentException("A structure can't have no center");
        }
        return dimensions;
    }

    @Override
    public MultiblockType<?> type() {
        return MultiblockTypeRegistry.DENSE;
    }

    @Override
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level level, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset) {
        BlockPos disp = forView
                ? new BlockPos(-this.viewOffX, -this.viewOffY + 1, -this.viewOffZ).rotate(rotation)
                : new BlockPos(-this.offX, -this.offY, -this.offZ).rotate(rotation);
        if (disableOffset)
            disp = BlockPos.ZERO;

        // the local origin of this multiblock, in world coordinates
        BlockPos origin = anchor.offset(disp);
        List<SimulateResult> ret = new ArrayList<>();
        for (int x = 0; x < this.size.getX(); x++) {
            for (int y = 0; y < this.size.getY(); y++) {
                for (int z = 0; z < this.size.getZ(); z++) {
                    BlockPos currDisp = new BlockPos(x, y, z).rotate(rotation);
                    BlockPos actionPos = origin.offset(currDisp);
                    char currC = this.pattern[y][x].charAt(z);
                    ret.add(new SimulateResultImpl(actionPos, this.stateMatchers[x][y][z], currC));
                }
            }
        }
        return Pair.of(origin, ret);
    }

    @Override
    public boolean test(Level level, BlockPos start, int x, int y, int z, Rotation rotation) {
        this.setLevel(level);
        if (x < 0 || y < 0 || z < 0 || x >= this.size.getX() || y >= this.size.getY() || z >= this.size.getZ()) {
            return false;
        }
        BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(AbstractMultiblock.fixHorizontal(rotation)));
        TriPredicate<BlockGetter, BlockPos, BlockState> pred = this.stateMatchers[x][y][z].getStatePredicate();
        BlockState state = level.getBlockState(checkPos).rotate(rotation);

        return pred.test(level, checkPos, state);
    }

    @Override
    public Vec3i getSize() {
        return this.size;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (x < 0 || y < 0 || z < 0 || x >= this.size.getX() || y >= this.size.getY() || z >= this.size.getZ()) {
            return Blocks.AIR.defaultBlockState();
        }
        long ticks = this.level != null ? this.level.getGameTime() : 0L;
        return this.stateMatchers[x][y][z].getDisplayedState(ticks);
    }
}
