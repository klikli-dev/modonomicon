/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.research.action.GiveItemAction;
import com.klikli_dev.modonomicon.research.action.GrantFactAction;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import com.klikli_dev.modonomicon.research.action.ResearchActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class ResearchActionTypeRegistry {

    private static final DispatchCodecRegistry<ResearchActionType<?>> TYPES =
            new DispatchCodecRegistry<>(ResearchActionType::id, "research action type");

    public static final ResearchActionType<GrantFactAction> GRANT_FACT = register(
            Modonomicon.loc("grant_fact"),
            GrantFactAction.MAP_CODEC,
            GrantFactAction.STREAM_CODEC
    );

    public static final ResearchActionType<GiveItemAction> GIVE_ITEM = register(
            Modonomicon.loc("give_item"),
            GiveItemAction.MAP_CODEC,
            GiveItemAction.STREAM_CODEC
    );

    private ResearchActionTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends ResearchAction> ResearchActionType<T> register(
            Identifier id,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return TYPES.register(id, new ResearchActionType<>(id, codec, streamCodec));
    }

    public static Codec<ResearchActionType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ResearchActionType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
