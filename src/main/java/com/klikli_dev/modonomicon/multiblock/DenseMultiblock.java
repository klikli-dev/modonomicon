/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 Authors of Patchouli
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

import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class DenseMultiblock extends AbstractMultiblock {

	private final String[][] pattern;
	private StateMatcher[][][] stateTargets;
	private final Vec3i size;

	public DenseMultiblock(String[][] pattern, Map<Character, StateMatcher> targets) {
		this.pattern = pattern;
		this.size = build(targets, getPatternDimensions(pattern));
	}

	public DenseMultiblock(String[][] pattern, Object... targets) {
		this.pattern = pattern;
		this.size = build(targetsToMatchers(targets), getPatternDimensions(pattern));
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
		BlockPos disp = forView
				? new BlockPos(-viewOffX, -viewOffY + 1, -viewOffZ).rotate(rotation)
				: new BlockPos(-offX, -offY, -offZ).rotate(rotation);
		// the local origin of this multiblock, in world coordinates
		BlockPos origin = anchor.offset(disp);
		List<SimulateResult> ret = new ArrayList<>();
		for (int x = 0; x < size.getX(); x++) {
			for (int y = 0; y < size.getY(); y++) {
				for (int z = 0; z < size.getZ(); z++) {
					BlockPos currDisp = new BlockPos(x, y, z).rotate(rotation);
					BlockPos actionPos = origin.offset(currDisp);
					char currC = pattern[y][x].charAt(z);
					ret.add(new SimulateResultImpl(actionPos, stateTargets[x][y][z], currC));
				}
			}
		}
		return Pair.of(origin, ret);
	}

	@Override
	public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
		setWorld(world);
		if (x < 0 || y < 0 || z < 0 || x >= size.getX() || y >= size.getY() || z >= size.getZ()) {
			return false;
		}
		BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(AbstractMultiblock.fixHorizontal(rotation)));
		TriPredicate<BlockGetter, BlockPos, BlockState> pred = stateTargets[x][y][z].getStatePredicate();
		BlockState state = world.getBlockState(checkPos).rotate(rotation);

		return pred.test(world, checkPos, state);
	}

	private static Map<Character, StateMatcher> targetsToMatchers(Object... targets) {
		if (targets.length % 2 == 1) {
			throw new IllegalArgumentException("Illegal argument length for targets array " + targets.length);
		}
		Map<Character, StateMatcher> stateMap = new HashMap<>();
		for (int i = 0; i < targets.length / 2; i++) {
			char c = (Character) targets[i * 2];
			Object o = targets[i * 2 + 1];
			StateMatcher state;

			if (o instanceof Block) {
				state = AbstractStateMatcher.fromBlockLoose((Block) o);
			} else if (o instanceof BlockState) {
				state = AbstractStateMatcher.fromState((BlockState) o);
			} else if (o instanceof String) {
				try {
					state = StringStateMatcher.fromString((String) o);
				} catch (CommandSyntaxException e) {
					throw new RuntimeException(e);
				}
			} else if (o instanceof StateMatcher) {
				state = (StateMatcher) o;
			} else {
				throw new IllegalArgumentException("Invalid target " + o);
			}

			stateMap.put(c, state);
		}

		if (!stateMap.containsKey('_')) {
			stateMap.put('_', AbstractStateMatcher.ANY);
		}
		if (!stateMap.containsKey(' ')) {
			stateMap.put(' ', AbstractStateMatcher.AIR);
		}
		if (!stateMap.containsKey('0')) {
			stateMap.put('0', AbstractStateMatcher.AIR);
		}
		return stateMap;
	}

	private Vec3i build(Map<Character, StateMatcher> stateMap, Vec3i dimensions) {
		boolean foundCenter = false;

		stateTargets = new StateMatcher[dimensions.getX()][dimensions.getY()][dimensions.getZ()];
		for (int y = 0; y < dimensions.getY(); y++) {
			for (int x = 0; x < dimensions.getX(); x++) {
				for (int z = 0; z < dimensions.getZ(); z++) {
					char c = pattern[y][x].charAt(z);
					if (!stateMap.containsKey(c)) {
						throw new IllegalArgumentException("Character " + c + " isn't mapped");
					}

					StateMatcher matcher = stateMap.get(c);
					if (c == '0') {
						if (foundCenter) {
							throw new IllegalArgumentException("A structure can't have two centers");
						}
						foundCenter = true;
						offX = x;
						offY = dimensions.getY() - y - 1;
						offZ = z;
						setViewOffset();
					}

					stateTargets[x][dimensions.getY() - y - 1][z] = matcher;
				}
			}
		}

		if (!foundCenter) {
			throw new IllegalArgumentException("A structure can't have no center");
		}
		return dimensions;
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

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (x < 0 || y < 0 || z < 0 || x >= size.getX() || y >= size.getY() || z >= size.getZ()) {
			return Blocks.AIR.defaultBlockState();
		}
		long ticks = world != null ? world.getGameTime() : 0L;
		return stateTargets[x][y][z].getDisplayedState(ticks);
	}

	@Override
	public Vec3i getSize() {
		return size;
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
