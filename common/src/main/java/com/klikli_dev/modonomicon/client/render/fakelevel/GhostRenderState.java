/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.fakelevel;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Adapted from FramedBlocks' GhostBlockRenderer ghost render state.
 * Source: https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/client/render/special/GhostBlockRenderer.java
 */
public record GhostRenderState(
        ClientLevel realLevel,
        BlockPos pos,
        BlockState state
) implements DelegatingBlockRenderFakeLevel { }
