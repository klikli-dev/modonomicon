/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Registered type of a {@link ResearchReward}, holding the codecs for its parameters.
 *
 * @param id unique identifier of the reward type, e.g. {@code modonomicon:item}
 * @param codec codec for the reward parameters, the {@code type} field is handled by the dispatch
 * @param streamCodec network codec for the reward
 */
public record ResearchRewardType<T extends ResearchReward>(
        Identifier id,
        MapCodec<T> codec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) {
}
