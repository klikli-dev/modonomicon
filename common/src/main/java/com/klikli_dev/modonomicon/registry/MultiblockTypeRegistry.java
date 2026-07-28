/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.data.MultiblockType;
import com.klikli_dev.modonomicon.multiblock.DenseMultiblock;
import com.klikli_dev.modonomicon.multiblock.SparseMultiblock;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class MultiblockTypeRegistry {

    private static final DispatchCodecRegistry<MultiblockType<?>> TYPES = new DispatchCodecRegistry<>(MultiblockType::id, "multiblock type");

    public static final MultiblockType<DenseMultiblock> DENSE = register(DenseMultiblock.ID, DenseMultiblock.CODEC, DenseMultiblock.STREAM_CODEC);

    public static final MultiblockType<SparseMultiblock> SPARSE = register(SparseMultiblock.ID, SparseMultiblock.CODEC, SparseMultiblock.STREAM_CODEC);

    private MultiblockTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends com.klikli_dev.modonomicon.api.multiblock.Multiblock> MultiblockType<T> register(Identifier id, com.mojang.serialization.MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return TYPES.register(id, new MultiblockType<>(id, codec, streamCodec));
    }

    public static com.mojang.serialization.Codec<MultiblockType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, MultiblockType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
