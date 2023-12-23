/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.world.level.block.Blocks;

public class Matchers {
    public static final AnyMatcher ANY = new AnyMatcher();

    public static final PredicateMatcher AIR = new PredicateMatcher(Blocks.AIR.defaultBlockState(), Modonomicon.loc("air"), false);

}
