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

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class SimulateResultImpl implements Multiblock.SimulateResult {
    private final BlockPos worldPosition;
    private final StateMatcher stateMatcher;
    @Nullable
    private final Character character;

    public SimulateResultImpl(BlockPos worldPosition, StateMatcher stateMatcher, @Nullable Character character) {
        this.worldPosition = worldPosition;
        this.stateMatcher = stateMatcher;
        this.character = character;
    }

    @Override
    public BlockPos getWorldPosition() {
        return this.worldPosition;
    }

    @Override
    public StateMatcher getStateMatcher() {
        return this.stateMatcher;
    }

    @Nullable
    @Override
    public Character getCharacter() {
        return this.character;
    }

    @Override
    public boolean test(Level world, Rotation rotation) {
        //{@link BlockState#rotate(LevelAccessor, BlockPos, Rotation)} */
        var pos = this.getWorldPosition();
        BlockState state = world.getBlockState(pos).rotate(world, pos, AbstractMultiblock.fixHorizontal(rotation));
        return this.getStateMatcher().getStatePredicate().test(world, pos, state);
    }
}
