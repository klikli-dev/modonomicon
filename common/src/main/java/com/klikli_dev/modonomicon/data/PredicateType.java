/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public record PredicateType(
        Identifier id,
        TriPredicate<BlockGetter, BlockPos, BlockState> predicate
) {
}
