/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;

/**
 * How a research stage completes.
 * <ul>
 *     <li>{@link Mode#AUTO}: the stage completes as soon as its requirements are met (default).</li>
 *     <li>{@link Mode#MANUAL}: the stage stays current once its requirements are met. The player completes it with
 *     {@code ResearchStages.completeManual}, which consumes the cost.</li>
 * </ul>
 *
 * @param mode how the stage completes
 * @param cost the cost consumed when a manual stage is completed, always empty for automatic stages
 */
public record ResearchStageCompletion(Mode mode, ResearchCost cost) {

    public static final ResearchStageCompletion AUTO = new ResearchStageCompletion(Mode.AUTO, ResearchCost.EMPTY);

    public static ResearchStageCompletion manual(ResearchCost cost) {
        return new ResearchStageCompletion(Mode.MANUAL, cost);
    }

    public boolean isManual() {
        return this.mode == Mode.MANUAL;
    }

    public enum Mode {
        AUTO("auto"),
        MANUAL("manual");

        public static final Codec<Mode> CODEC = Codec.STRING.comapFlatMap(
                id -> Arrays.stream(values())
                        .filter(mode -> mode.id.equals(id))
                        .findFirst()
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown stage completion mode '" + id + "'")),
                mode -> mode.id
        );

        private final String id;

        Mode(String id) {
            this.id = id;
        }
    }

    public static final Codec<ResearchStageCompletion> CODEC = RecordCodecBuilder.<ResearchStageCompletion>create(instance -> instance.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.AUTO).forGetter(ResearchStageCompletion::mode),
            ResearchCost.CODEC.optionalFieldOf("cost", ResearchCost.EMPTY).forGetter(ResearchStageCompletion::cost)
    ).apply(instance, ResearchStageCompletion::new)).validate(completion ->
            completion.mode() == Mode.AUTO && !completion.cost().isEmpty()
                    ? DataResult.error(() -> "A cost can only be set on a manual stage completion")
                    : DataResult.success(completion)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchStageCompletion> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT.map(ordinal -> Mode.values()[ordinal], Mode::ordinal),
                    ResearchStageCompletion::mode,
                    ResearchCost.STREAM_CODEC,
                    ResearchStageCompletion::cost,
                    ResearchStageCompletion::new
            );
}
