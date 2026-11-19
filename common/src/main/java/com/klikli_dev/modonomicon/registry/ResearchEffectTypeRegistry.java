/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.research.effect.AddValueEffect;
import com.klikli_dev.modonomicon.research.effect.ExperienceEffect;
import com.klikli_dev.modonomicon.research.effect.FunctionEffect;
import com.klikli_dev.modonomicon.research.effect.GiveItemEffect;
import com.klikli_dev.modonomicon.research.effect.GrantFactEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffectType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Registry of research effect types, keyed by id. Mods register their own effect types with {@link #register}
 * before research data is loaded, e.g. during mod setup.
 */
public final class ResearchEffectTypeRegistry {

    private static final DispatchCodecRegistry<ResearchEffectType<?>> TYPES =
            new DispatchCodecRegistry<>(ResearchEffectType::id, "research effect type");

    public static final ResearchEffectType<GrantFactEffect> GRANT_FACT = register(
            Modonomicon.loc("grant_fact"),
            GrantFactEffect.MAP_CODEC,
            GrantFactEffect.STREAM_CODEC
    );

    public static final ResearchEffectType<AddValueEffect> ADD_VALUE = register(
            Modonomicon.loc("add_value"),
            AddValueEffect.MAP_CODEC,
            AddValueEffect.STREAM_CODEC
    );

    public static final ResearchEffectType<GiveItemEffect> GIVE_ITEM = register(
            Modonomicon.loc("give_item"),
            GiveItemEffect.MAP_CODEC,
            GiveItemEffect.STREAM_CODEC
    );

    public static final ResearchEffectType<ExperienceEffect> EXPERIENCE = register(
            Modonomicon.loc("experience"),
            ExperienceEffect.MAP_CODEC,
            ExperienceEffect.STREAM_CODEC
    );

    public static final ResearchEffectType<FunctionEffect> FUNCTION = register(
            Modonomicon.loc("function"),
            FunctionEffect.MAP_CODEC,
            FunctionEffect.STREAM_CODEC
    );

    /**
     * Dispatches on the {@code type} field. Declared after the built-in types so they are registered first.
     */
    public static final Codec<ResearchEffect> CODEC = TYPES.byNameCodec()
            .dispatch("type", ResearchEffect::type, ResearchEffectType::codec);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchEffect> STREAM_CODEC = TYPES.byNameStreamCodec()
            .dispatch(ResearchEffect::type, ResearchEffectType::streamCodec);

    private ResearchEffectTypeRegistry() {
    }

    public static void bootstrap() {
    }

    /**
     * Registers an effect type. A duplicate id throws.
     */
    public static <T extends ResearchEffect> ResearchEffectType<T> register(
            Identifier id,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return TYPES.register(id, new ResearchEffectType<>(id, codec, streamCodec));
    }
}
