/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.research.reward.AddValueReward;
import com.klikli_dev.modonomicon.research.reward.ExperienceReward;
import com.klikli_dev.modonomicon.research.reward.FunctionReward;
import com.klikli_dev.modonomicon.research.reward.GrantFactReward;
import com.klikli_dev.modonomicon.research.reward.ItemReward;
import com.klikli_dev.modonomicon.research.reward.ResearchReward;
import com.klikli_dev.modonomicon.research.reward.ResearchRewardType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class ResearchRewardTypeRegistry {

    private static final DispatchCodecRegistry<ResearchRewardType<?>> TYPES =
            new DispatchCodecRegistry<>(ResearchRewardType::id, "research reward type");

    public static final ResearchRewardType<ItemReward> ITEM = register(
            Modonomicon.loc("item"),
            ItemReward.MAP_CODEC,
            ItemReward.STREAM_CODEC
    );

    public static final ResearchRewardType<ExperienceReward> EXPERIENCE = register(
            Modonomicon.loc("experience"),
            ExperienceReward.MAP_CODEC,
            ExperienceReward.STREAM_CODEC
    );

    public static final ResearchRewardType<GrantFactReward> GRANT_FACT = register(
            Modonomicon.loc("grant_fact"),
            GrantFactReward.MAP_CODEC,
            GrantFactReward.STREAM_CODEC
    );

    public static final ResearchRewardType<AddValueReward> ADD_VALUE = register(
            Modonomicon.loc("add_value"),
            AddValueReward.MAP_CODEC,
            AddValueReward.STREAM_CODEC
    );

    public static final ResearchRewardType<FunctionReward> FUNCTION = register(
            Modonomicon.loc("function"),
            FunctionReward.MAP_CODEC,
            FunctionReward.STREAM_CODEC
    );

    private ResearchRewardTypeRegistry() {
    }

    public static void bootstrap() {
    }

    public static <T extends ResearchReward> ResearchRewardType<T> register(
            Identifier id,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return TYPES.register(id, new ResearchRewardType<>(id, codec, streamCodec));
    }

    public static Codec<ResearchRewardType<?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ResearchRewardType<?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }
}
