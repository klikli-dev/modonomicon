/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * A single toast trigger sent from server to client.
 *
 * @param type fact, value, or node
 * @param elementId the research element's id
 * @param currentValue for values: the value after increment; 0 for facts/nodes
 */
public record ResearchToastTrigger(
        ToastTriggerType type,
        Identifier elementId,
        int currentValue
) {
    public enum ToastTriggerType {
        FACT_GRANTED,
        VALUE_INCREMENTED,
        NODE_UNLOCKED,
        NODE_STAGE_COMPLETED
    }

    private static final StreamCodec<RegistryFriendlyByteBuf, ToastTriggerType> TYPE_STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ToastTriggerType>() {
        @Override
        public ToastTriggerType decode(RegistryFriendlyByteBuf input) {
            return ToastTriggerType.values()[input.readVarInt()];
        }

        @Override
        public void encode(RegistryFriendlyByteBuf output, ToastTriggerType value) {
            output.writeVarInt(value.ordinal());
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchToastTrigger> STREAM_CODEC = StreamCodec.composite(
            TYPE_STREAM_CODEC,
            ResearchToastTrigger::type,
            Identifier.STREAM_CODEC,
            ResearchToastTrigger::elementId,
            ByteBufCodecs.VAR_INT,
            ResearchToastTrigger::currentValue,
            ResearchToastTrigger::new
    );
}
