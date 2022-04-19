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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SparseMultiblock extends AbstractMultiblock {
	private final Map<BlockPos, StateMatcher> data;
	private final Vec3i size;

	public SparseMultiblock(Map<BlockPos, StateMatcher> data) {
		Preconditions.checkArgument(!data.isEmpty(), "No data given to sparse multiblock!");
		this.data = ImmutableMap.copyOf(data);
		this.size = calculateSize();
	}

	@Override
	public Vec3i getSize() {
		return size;
	}

	private Vec3i calculateSize() {
		int minX = data.keySet().stream().mapToInt(BlockPos::getX).min().getAsInt();
		int maxX = data.keySet().stream().mapToInt(BlockPos::getX).max().getAsInt();
		int minY = data.keySet().stream().mapToInt(BlockPos::getY).min().getAsInt();
		int maxY = data.keySet().stream().mapToInt(BlockPos::getY).max().getAsInt();
		int minZ = data.keySet().stream().mapToInt(BlockPos::getZ).min().getAsInt();
		int maxZ = data.keySet().stream().mapToInt(BlockPos::getZ).max().getAsInt();
		return new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		long ticks = world != null ? world.getGameTime() : 0L;
		return data.getOrDefault(pos, AbstractStateMatcher.AIR).getDisplayedState(ticks);
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
		BlockPos disp = forView
				? new BlockPos(-viewOffX, -viewOffY + 1, -viewOffZ).rotate(rotation)
				: new BlockPos(-offX, -offY, -offZ).rotate(rotation);
		// the local origin of this multiblock, in world coordinates
		BlockPos origin = anchor.offset(disp);
		List<SimulateResult> ret = new ArrayList<>();
		for (var e : data.entrySet()) {
			BlockPos currDisp = e.getKey().rotate(rotation);
			BlockPos actionPos = origin.offset(currDisp);
			ret.add(new SimulateResultImpl(actionPos, e.getValue(), null));
		}
		return Pair.of(origin, ret);
	}

	@Override
	public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
		setWorld(world);
		BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(rotation));
		BlockState state = world.getBlockState(checkPos).rotate(AbstractMultiblock.fixHorizontal(rotation));
		StateMatcher matcher = data.getOrDefault(new BlockPos(x, y, z), AbstractStateMatcher.ANY);
		return matcher.getStatePredicate().test(world, checkPos, state);
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
