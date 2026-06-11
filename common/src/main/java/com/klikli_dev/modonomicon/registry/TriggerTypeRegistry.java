/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.DispatchCodecRegistry;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.hook.NoTriggerTarget;
import com.klikli_dev.modonomicon.research.hook.*;
import com.klikli_dev.modonomicon.research.hook.handlers.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.stream.Stream;

public final class TriggerTypeRegistry {

    private static final DispatchCodecRegistry<TriggerType<?, ?>> TYPES =
            new DispatchCodecRegistry<>(TriggerType::id, "trigger type");

    public static final TriggerType<Identifier, EntryViewedContext> ENTRY_VIEWED_ONCE = register(
            Modonomicon.loc("entry_viewed_once"),
            Identifier.CODEC,
            Identifier.STREAM_CODEC.cast(),
            new EntryViewedOnceTriggerHandler()
    );

    public static final TriggerType<ItemStackTemplate, ItemCraftedContext> ITEM_CRAFTED = register(
            Modonomicon.loc("item_crafted"),
            ItemStackTemplate.CODEC,
            ItemStackTemplate.STREAM_CODEC.cast(),
            new ItemCraftedTriggerHandler()
    );

    public static final TriggerType<ItemStackTemplate, ItemAcquiredContext> ITEM_ACQUIRED = register(
            Modonomicon.loc("item_acquired"),
            ItemStackTemplate.CODEC,
            ItemStackTemplate.STREAM_CODEC.cast(),
            new ItemAcquiredTriggerHandler()
    );

    public static final TriggerType<Identifier, AdvancementContext> ADVANCEMENT = register(
            Modonomicon.loc("advancement"),
            Identifier.CODEC,
            Identifier.STREAM_CODEC.cast(),
            new AdvancementTriggerHandler()
    );

    private TriggerTypeRegistry() {
    }

    public static void bootstrap() {
    }

    @SuppressWarnings("unchecked")
    public static <TTarget, TContext extends TriggerContext> TriggerType<TTarget, TContext> register(
            Identifier id,
            Codec<TTarget> codec,
            StreamCodec<RegistryFriendlyByteBuf, TTarget> streamCodec,
            TriggerHandler<TTarget, TContext> handler
    ) {
        var type = (TriggerType<TTarget, TContext>) (TriggerType<?, ?>)
                TYPES.register(id, new TriggerType<>(id, codec.fieldOf("trigger_target"), streamCodec, handler));
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <TContext extends TriggerContext> TriggerType<NoTriggerTarget, TContext> registerTargetless(
            Identifier id,
            TriggerHandler<NoTriggerTarget, TContext> handler
    ) {
        var type = (TriggerType<NoTriggerTarget, TContext>) (TriggerType<?, ?>)
                TYPES.register(id, new TriggerType<>(id, targetlessTargetCodec(id), StreamCodec.unit(NoTriggerTarget.INSTANCE), handler));
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <TTarget, TContext extends TriggerContext> TriggerHandler<TTarget, TContext> handler(
            TriggerType<TTarget, TContext> type
    ) {
        return type.handler();
    }

    public static Codec<TriggerType<?, ?>> codec() {
        return TYPES.byNameCodec();
    }

    public static StreamCodec<RegistryFriendlyByteBuf, TriggerType<?, ?>> streamCodec() {
        return TYPES.byNameStreamCodec();
    }

    private static MapCodec<NoTriggerTarget> targetlessTargetCodec(Identifier id) {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString("trigger_target"));
            }

            @Override
            public <T> DataResult<NoTriggerTarget> decode(DynamicOps<T> ops, MapLike<T> input) {
                if (input.get("trigger_target") != null) {
                    return DataResult.error(() -> "Trigger type '" + id + "' does not accept trigger_target data");
                }
                return DataResult.success(NoTriggerTarget.INSTANCE);
            }

            @Override
            public <T> RecordBuilder<T> encode(NoTriggerTarget input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            }
        };
    }
}
