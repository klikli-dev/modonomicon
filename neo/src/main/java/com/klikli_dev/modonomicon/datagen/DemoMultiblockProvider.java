// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;

public class DemoMultiblockProvider extends MultiblockProvider {
    public DemoMultiblockProvider(PackOutput packOutput, String modid) {
        super(packOutput, modid);
    }

    @Override
    public void buildMultiblocks() {
        this.add(this.modLoc("demo_dense"), new DenseMultiblockBuilder()
                .layer(" WWW ",
                        "N   S",
                        "N 0 S",
                        "N   S",
                        " EEE "
                )
                .blockstate('N', () -> Blocks.OAK_STAIRS, "[facing=north]")
                .blockstate('S', () -> Blocks.OAK_STAIRS, "[facing=south]")
                .blockstate('W', () -> Blocks.OAK_STAIRS, "[facing=west]")
                .blockstate('E', () -> Blocks.OAK_STAIRS, "[facing=east]")


        );
    }
}
