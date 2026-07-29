/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Defines a research node that unlocks when all required facts are present
 * AND all required values have reached their thresholds AND all required stage
 * dependencies on other nodes are satisfied.
 *
 * Multi-stage nodes have an ordered list of stages. The node activates when base
 * requirements are met, then progresses through stages automatically. The node is
 * fully complete only when all stages are done.
 *
 * @param id unique identifier for this node
 * @param requiredFacts fact ids that must be granted for this node to activate
 * @param requiredValues value requirements that must be met for this node to activate
 * @param stages ordered stages; empty list means single-stage (node activates = complete)
 * @param requiredStages dependencies on other nodes reaching specific stages
 * @param toast optional toast display data for full node completion
 */
public record ResearchNodeDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ValueRequirement> requiredValues,
        List<ResearchStageDefinition> stages,
        List<StageDependency> requiredStages,
        Optional<ResearchToastDefinition> toast
) {

    /**
     * Dependency on another node reaching a specific stage.
     *
     * @param nodeId the node that must reach the specified stage
     * @param stageId the stage within that node that must be completed
     */
    public record StageDependency(Identifier nodeId, Identifier stageId) {
        public static final Codec<StageDependency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("node_id").forGetter(StageDependency::nodeId),
                Identifier.CODEC.fieldOf("stage_id").forGetter(StageDependency::stageId)
        ).apply(instance, StageDependency::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, StageDependency> STREAM_CODEC =
                StreamCodec.composite(
                        Identifier.STREAM_CODEC,
                        StageDependency::nodeId,
                        Identifier.STREAM_CODEC,
                        StageDependency::stageId,
                        StageDependency::new
                );
    }

    public record ValueRequirement(Identifier valueId, int threshold) {
        public static final Codec<ValueRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("value_id").forGetter(ValueRequirement::valueId),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("threshold").forGetter(ValueRequirement::threshold)
        ).apply(instance, ValueRequirement::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ValueRequirement> STREAM_CODEC =
                StreamCodec.composite(
                        Identifier.STREAM_CODEC,
                        ValueRequirement::valueId,
                        ByteBufCodecs.VAR_INT,
                        ValueRequirement::threshold,
                        ValueRequirement::new
                );
    }

    public Optional<ResearchToastDefinition> toastOrEmpty() {
        return this.toast;
    }

    public static final Codec<ResearchNodeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchNodeDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchNodeDefinition::requiredFacts),
            ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchNodeDefinition::requiredValues),
            ResearchStageDefinition.CODEC.listOf().optionalFieldOf("stages", List.of()).forGetter(ResearchNodeDefinition::stages),
            StageDependency.CODEC.listOf().optionalFieldOf("required_stages", List.of()).forGetter(ResearchNodeDefinition::requiredStages),
            ResearchToastDefinition.CODEC.optionalFieldOf("toast").forGetter(ResearchNodeDefinition::toastOrEmpty)
    ).apply(instance, ResearchNodeDefinition::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNodeDefinition> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    ResearchNodeDefinition::id,
                    ByteBufCodecs.collection(ArrayList::new, Identifier.STREAM_CODEC),
                    ResearchNodeDefinition::requiredFacts,
                    ByteBufCodecs.collection(ArrayList::new, ValueRequirement.STREAM_CODEC),
                    ResearchNodeDefinition::requiredValues,
                    ByteBufCodecs.collection(ArrayList::new, ResearchStageDefinition.STREAM_CODEC),
                    ResearchNodeDefinition::stages,
                    ByteBufCodecs.collection(ArrayList::new, StageDependency.STREAM_CODEC),
                    ResearchNodeDefinition::requiredStages,
                    ByteBufCodecs.optional(ResearchToastDefinition.STREAM_CODEC),
                    ResearchNodeDefinition::toast,
                    ResearchNodeDefinition::new
            );
}
