/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.multiblock.matcher;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import com.klikli_dev.modonomicon.data.StateMatcherType;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

/**
 * Matches any block, including air, and does not display anything.
 */
public class AnyMatcher extends DisplayOnlyMatcher {

    public static final Identifier ID = Modonomicon.loc("any");
    public static final MapCodec<AnyMatcher> CODEC = MapCodec.unit(new AnyMatcher());
    public static final StreamCodec<RegistryFriendlyByteBuf, AnyMatcher> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    protected AnyMatcher() {
        super(Blocks.AIR.defaultBlockState());
    }

    @Override
    public StateMatcherType<?> type() {
        return StateMatcherTypeRegistry.ANY;
    }
}
