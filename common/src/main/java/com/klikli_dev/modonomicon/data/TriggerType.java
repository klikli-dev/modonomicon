/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Type descriptor for a research hook trigger.
 *
 * @param id the unique identifier for this trigger type
 * @param codec the codec for trigger-specific data
 * @param streamCodec the network codec for trigger-specific data
 */
public record TriggerType(
        Identifier id,
        MapCodec<?> codec,
        StreamCodec<RegistryFriendlyByteBuf, ?> streamCodec
) {
}
