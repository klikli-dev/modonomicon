/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.research.cost.DurabilityCostEntry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.LevelsCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Registry of research cost types, keyed by id. Mods register their own cost types with {@link #register}
 * before research data is loaded, e.g. during mod setup.
 */
public final class ResearchCostTypeRegistry {

    private static final DispatchCodecRegistry<ResearchCostType<?>> TYPES =
            new DispatchCodecRegistry<>(ResearchCostType::id, "research cost type");

    public static final ResearchCostType<ItemCostEntry> ITEM = register(
            Modonomicon.loc("item"),
            ItemCostEntry.MAP_CODEC,
            ItemCostEntry.STREAM_CODEC
    );

    public static final ResearchCostType<DurabilityCostEntry> DURABILITY = register(
            Modonomicon.loc("durability"),
            DurabilityCostEntry.MAP_CODEC,
            DurabilityCostEntry.STREAM_CODEC
    );

    public static final ResearchCostType<LevelsCostEntry> LEVELS = register(
            Modonomicon.loc("levels"),
            LevelsCostEntry.MAP_CODEC,
            LevelsCostEntry.STREAM_CODEC
    );

    /**
     * Dispatches on the {@code type} field. Declared after the built-in types so they are registered first.
     */
    public static final Codec<ResearchCostEntry> CODEC = TYPES.byNameCodec()
            .dispatch("type", ResearchCostEntry::type, ResearchCostType::codec);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCostEntry> STREAM_CODEC = TYPES.byNameStreamCodec()
            .dispatch(ResearchCostEntry::type, ResearchCostType::streamCodec);

    private ResearchCostTypeRegistry() {
    }

    public static void bootstrap() {
    }

    /**
     * Registers a cost type. A duplicate id throws.
     */
    public static <T extends ResearchCostEntry> ResearchCostType<T> register(
            Identifier id,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return TYPES.register(id, new ResearchCostType<>(id, codec, streamCodec));
    }
}
