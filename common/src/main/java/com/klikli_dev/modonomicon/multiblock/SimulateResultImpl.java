/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
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
        var pos = this.getWorldPosition();
        BlockState state = world.getBlockState(pos).rotate(AbstractMultiblock.fixHorizontal(rotation));
        return this.getStateMatcher().getStatePredicate().test(world, pos, state);
    }
}
