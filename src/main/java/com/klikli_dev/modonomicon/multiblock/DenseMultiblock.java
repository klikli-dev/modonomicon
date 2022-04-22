/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 Authors of Patchouli, klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.multiblock;

import com.google.gson.*;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.klikli_dev.modonomicon.registry.StateMatcherLoaderRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.Map.Entry;

public class DenseMultiblock extends AbstractMultiblock {

    private static final Gson GSON = new GsonBuilder().create();

    private final String[][] pattern;
    private final Vec3i size;
    private StateMatcher[][][] stateMatchers;

    public DenseMultiblock(String[][] pattern, Map<Character, StateMatcher> targets) {
        this.pattern = pattern;
        this.size = this.build(targets, getPatternDimensions(pattern));
    }

    public static DenseMultiblock fromJson(JsonObject json) {
        var pattern = GSON.fromJson(json.get("pattern"), String[][].class);

        var jsonMapping = GsonHelper.getAsJsonObject(json, "mapping");

        Map<Character, StateMatcher> mapping = new HashMap<>();
        for (Entry<String, JsonElement> entry : jsonMapping.entrySet()) {
            if (entry.getKey().length() != 1)
                throw new JsonSyntaxException("Mapping key needs to be only 1 character");
            char key = entry.getKey().charAt(0);
            var value = entry.getValue().getAsJsonObject();
            var stateMatcherType = ResourceLocation.tryParse(GsonHelper.getAsString(value, "type"));
            var stateMatcher = StateMatcherLoaderRegistry.getStateMatcherJsonLoader(stateMatcherType).fromJson(value);
            mapping.put(key, stateMatcher);
        }

        return new DenseMultiblock(pattern, mapping);
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
                    throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);
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
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
        BlockPos disp = forView
                ? new BlockPos(-this.viewOffX, -this.viewOffY + 1, -this.viewOffZ).rotate(rotation)
                : new BlockPos(-this.offX, -this.offY, -this.offZ).rotate(rotation);
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
    public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
        this.setWorld(world);
        if (x < 0 || y < 0 || z < 0 || x >= this.size.getX() || y >= this.size.getY() || z >= this.size.getZ()) {
            return false;
        }
        BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(AbstractMultiblock.fixHorizontal(rotation)));
        TriPredicate<BlockGetter, BlockPos, BlockState> pred = this.stateMatchers[x][y][z].getStatePredicate();
        BlockState state = world.getBlockState(checkPos).rotate(rotation);

        return pred.test(world, checkPos, state);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (x < 0 || y < 0 || z < 0 || x >= this.size.getX() || y >= this.size.getY() || z >= this.size.getZ()) {
            return Blocks.AIR.defaultBlockState();
        }
        long ticks = this.world != null ? this.world.getGameTime() : 0L;
        return this.stateMatchers[x][y][z].getDisplayedState(ticks);
    }

    @Override
    public Vec3i getSize() {
        return this.size;
    }

    // These heights were assumed based being derivative of old behavior, but it may be ideal to change
    @Override
    public int getHeight() {
        return 255;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }
}
