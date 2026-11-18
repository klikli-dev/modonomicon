/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Registered type of a {@link ResearchAction}, holding the codecs for its parameters.
 *
 * @param id unique identifier of the action type, e.g. {@code modonomicon:grant_fact}
 * @param codec codec for the action parameters, the {@code type} field is handled by the dispatch
 * @param streamCodec network codec for the action
 */
public record ResearchActionType<T extends ResearchAction>(
        Identifier id,
        MapCodec<T> codec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) {
}
