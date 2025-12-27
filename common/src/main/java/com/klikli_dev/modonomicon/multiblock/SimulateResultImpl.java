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


public record SimulateResultImpl(BlockPos worldPosition, StateMatcher stateMatcher,
                                 @Nullable Character character) implements Multiblock.SimulateResult {

    @Override
    public boolean test(Level world, Rotation rotation) {
        var pos = this.worldPosition();
        BlockState state = world.getBlockState(pos).rotate(AbstractMultiblock.fixHorizontal(rotation));
        return this.stateMatcher().getStatePredicate().test(world, pos, state);
    }
}
