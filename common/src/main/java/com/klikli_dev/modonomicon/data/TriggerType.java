/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Type descriptor for a research hook trigger.
 *
 * @param id the unique identifier for this trigger type
 * @param targetCodec the directly usable codec for the hook's `trigger_target` field
 * @param targetStreamCodec the network codec for trigger-specific target data
 * @param handler the handler that resolves and matches hooks for this trigger type
 */
public record TriggerType<TTarget, TContext extends TriggerContext>(
        Identifier id,
        MapCodec<TTarget> targetCodec,
        StreamCodec<RegistryFriendlyByteBuf, TTarget> targetStreamCodec,
        TriggerHandler<TTarget, TContext> handler
) {
}
