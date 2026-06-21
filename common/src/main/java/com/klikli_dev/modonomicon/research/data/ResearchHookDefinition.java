/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Defines an authored hook that maps one explicit runtime event target to a research mutation.
 * A hook either grants a fact OR increments a value, never both.
 *
 * @param id unique id of this hook definition resource
 * @param triggerType the supported event family that can fire this hook
 * @param triggerTarget the typed target condition for this hook (e.g. Identifier for entry/advancement, ItemStackTemplate for items)
 * @param factId the research fact granted when the configured trigger fires (null if this hook increments a value)
 * @param valueId the research value incremented when the configured trigger fires (null if this hook grants a fact)
 * @param increment the amount to add to the target value when this hook fires (default 1, ignored for fact hooks)
 */
public record ResearchHookDefinition<TTarget>(
        Identifier id,
        TriggerType<TTarget, ?> triggerType,
        TTarget triggerTarget,
        @Nullable Identifier factId,
        @Nullable Identifier valueId,
        int increment
) {
    private static final String ID = "id";
    private static final String TRIGGER_TYPE = "trigger_type";
    private static final String TRIGGER_TARGET = "trigger_target";
    private static final String FACT_ID = "fact_id";
    private static final String VALUE_ID = "value_id";
    private static final String INCREMENT = "increment";

    private static final MapCodec<Identifier> ID_CODEC = Identifier.CODEC.fieldOf(ID);
    private static final MapCodec<TriggerType<?, ?>> TRIGGER_TYPE_CODEC = TriggerTypeRegistry.codec().fieldOf(TRIGGER_TYPE);
    private static final MapCodec<Identifier> FACT_ID_CODEC = Identifier.CODEC.fieldOf(FACT_ID);
    private static final MapCodec<Identifier> VALUE_ID_CODEC = Identifier.CODEC.fieldOf(VALUE_ID);
    private static final MapCodec<Integer> INCREMENT_CODEC = Codec.INT.fieldOf(INCREMENT);

    public static final MapCodec<ResearchHookDefinition<?>> MAP_CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(
                    ops.createString(ID),
                    ops.createString(TRIGGER_TYPE),
                    ops.createString(TRIGGER_TARGET),
                    ops.createString(FACT_ID),
                    ops.createString(VALUE_ID),
                    ops.createString(INCREMENT)
            );
        }

        @Override
        public <T> DataResult<ResearchHookDefinition<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            return decodeRequired(ID_CODEC, ops, input).flatMap(id ->
                    decodeRequired(TRIGGER_TYPE_CODEC, ops, input).flatMap(type -> decodeTyped(ops, input, id, type))
            );
        }

        @Override
        public <T> RecordBuilder<T> encode(ResearchHookDefinition<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            ID_CODEC.encode(input.id(), ops, prefix);
            TRIGGER_TYPE_CODEC.encode(input.triggerType(), ops, prefix);
            return encodeTyped(input, ops, prefix);
        }
    };

    public static final Codec<ResearchHookDefinition<?>> CODEC = MAP_CODEC.codec();

    private static <T, TTarget> DataResult<ResearchHookDefinition<?>> decodeTyped(
            DynamicOps<T> ops,
            MapLike<T> input,
            Identifier id,
            TriggerType<TTarget, ?> triggerType
    ) {
        return decodeRequired(triggerType.targetCodec(), ops, input).flatMap(triggerTarget ->
                decodeOptional(Identifier.CODEC, ops, input, FACT_ID, null).flatMap(factId ->
                        decodeOptional(Identifier.CODEC, ops, input, VALUE_ID, null).flatMap(valueId ->
                                decodeOptional(Codec.INT, ops, input, INCREMENT, 1).map(increment ->
                                        (ResearchHookDefinition<?>) new ResearchHookDefinition<>(
                                                id,
                                                triggerType,
                                                triggerTarget,
                                                factId,
                                                valueId,
                                                increment
                                        )
                                )
                        )
                )
        );
    }

    private static <T, TTarget> RecordBuilder<T> encodeTyped(
            ResearchHookDefinition<?> input,
            DynamicOps<T> ops,
            RecordBuilder<T> prefix
    ) {
        var typedInput = cast(input);
        typedInput.triggerType().targetCodec().encode(typedInput.triggerTarget(), ops, prefix);
        if (typedInput.factId() != null) {
            FACT_ID_CODEC.encode(typedInput.factId(), ops, prefix);
        }
        if (typedInput.valueId() != null) {
            VALUE_ID_CODEC.encode(typedInput.valueId(), ops, prefix);
        }
        if (typedInput.increment() != 1) {
            INCREMENT_CODEC.encode(typedInput.increment(), ops, prefix);
        }
        return prefix;
    }

    private static <T, A> DataResult<A> decodeRequired(MapCodec<A> codec, DynamicOps<T> ops, MapLike<T> input) {
        return codec.decode(ops, input);
    }

    private static <T, A> DataResult<A> decodeOptional(Codec<A> codec, DynamicOps<T> ops, MapLike<T> input, String key, A defaultValue) {
        T value = input.get(key);
        if (value == null) {
            return DataResult.success(defaultValue);
        }

        return codec.parse(ops, value);
    }

    @SuppressWarnings("unchecked")
    private static <TTarget> ResearchHookDefinition<TTarget> cast(ResearchHookDefinition<?> input) {
        return (ResearchHookDefinition<TTarget>) Objects.requireNonNull(input);
    }
}
