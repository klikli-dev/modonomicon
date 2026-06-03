/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class TriggerTypeRegistry {

    private static final DispatchCodecRegistry<TriggerType> TYPES = new DispatchCodecRegistry<>(TriggerType::id, "trigger type");

    public static final TriggerType ENTRY_VIEWED_ONCE = register(
            Modonomicon.loc("entry_viewed_once"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast()
    );

    public static final TriggerType ITEM_CRAFTED = register(
            Modonomicon.loc("item_crafted"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast()
    );

    public static final TriggerType ITEM_ACQUIRED = register(
            Modonomicon.loc("item_acquired"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast()
    );

    private TriggerTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static TriggerType register(Identifier id, MapCodec<?> codec, StreamCodec<RegistryFriendlyByteBuf, ?> streamCodec) {
        return TYPES.register(id, new TriggerType(id, codec, streamCodec));
    }

    public static Codec<TriggerType> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, TriggerType> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
