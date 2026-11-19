/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Registered type of a {@link ResearchEffect}, holding the codecs for its parameters.
 *
 * @param id unique identifier of the effect type, e.g. {@code modonomicon:grant_fact}
 * @param codec codec for the effect parameters, the {@code type} field is handled by the dispatch
 * @param streamCodec network codec for the effect
 */
public record ResearchEffectType<T extends ResearchEffect>(
        Identifier id,
        MapCodec<T> codec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) {
}
