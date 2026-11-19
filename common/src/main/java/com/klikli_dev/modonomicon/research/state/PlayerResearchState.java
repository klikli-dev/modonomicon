/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Per-player research progress: facts, unlocked nodes, values and node stage progress.
 *
 * <p>Node stage progress is stored as a 1-based value (see {@link #getNodeStageIndex(Identifier)}), while stage rules
 * use 0-based stage indices. The stage methods of this class translate between the two, so callers should use them
 * instead of doing the arithmetic themselves:
 * <ul>
 *     <li>{@link #completedStageCount(Identifier)}: how many stages of a node are completed</li>
 *     <li>{@link #currentStageIndex(Identifier)}: 0-based index of the stage that is in progress next</li>
 *     <li>{@link #isStageCompleted(Identifier, int)}: whether the stage with a 0-based index is completed</li>
 * </ul>
 * The stored representation (and the NBT/JSON codec) is unchanged by these helpers.
 */
public class PlayerResearchState {

    public static final Codec<PlayerResearchState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.set(Identifier.CODEC).optionalFieldOf("facts", Set.of()).forGetter(PlayerResearchState::factIds),
            Codecs.set(Identifier.CODEC).optionalFieldOf("unlocked_nodes", Set.of()).forGetter(PlayerResearchState::unlockedNodeIds),
            Codecs.identifierIntMap().optionalFieldOf("values", Map.of()).forGetter(PlayerResearchState::valuesMap),
            Codecs.identifierIntMap().optionalFieldOf("node_stages", Map.of()).forGetter(PlayerResearchState::nodeStagesMap),
            Codecs.set(Identifier.CODEC).optionalFieldOf("rewarded_stages", Set.of()).forGetter(PlayerResearchState::rewardedStageIds)
    ).apply(instance, PlayerResearchState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResearchState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Set<Identifier> factIds;
    private final Set<Identifier> unlockedNodeIds;
    private final Object2IntOpenHashMap<Identifier> internalValues;
    private final Object2IntOpenHashMap<Identifier> nodeStageIndexes;
    private final Set<Identifier> rewardedStageIds;

    public PlayerResearchState() {
        this(Set.of(), Set.of(), Map.of(), Map.of(), Set.of());
    }

    /**
     * @deprecated use {@link #PlayerResearchState(Set, Set, Map, Map, Set)}
     */
    @Deprecated
    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds) {
        this(factIds, unlockedNodeIds, Map.of(), Map.of(), Set.of());
    }

    /**
     * @deprecated use {@link #PlayerResearchState(Set, Set, Map, Map, Set)}
     */
    @Deprecated
    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values) {
        this(factIds, unlockedNodeIds, values, Map.of(), Set.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values, Map<Identifier, Integer> nodeStages) {
        this(factIds, unlockedNodeIds, values, nodeStages, Set.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values, Map<Identifier, Integer> nodeStages, Set<Identifier> rewardedStageIds) {
        this.factIds = new ObjectOpenHashSet<>(factIds);
        this.unlockedNodeIds = new ObjectOpenHashSet<>(unlockedNodeIds);
        this.internalValues = new Object2IntOpenHashMap<>(values);
        this.nodeStageIndexes = new Object2IntOpenHashMap<>(nodeStages);
        this.rewardedStageIds = new ObjectOpenHashSet<>(rewardedStageIds);
    }

    public boolean grantFact(Identifier factId) {
        return this.factIds.add(factId);
    }

    public boolean revokeFact(Identifier factId) {
        return this.factIds.remove(factId);
    }

    public boolean hasFact(Identifier factId) {
        return this.factIds.contains(factId);
    }

    public boolean unlockNode(Identifier nodeId) {
        return this.unlockedNodeIds.add(nodeId);
    }

    public boolean lockNode(Identifier nodeId) {
        return this.unlockedNodeIds.remove(nodeId);
    }

    public boolean isNodeUnlocked(Identifier nodeId) {
        return this.unlockedNodeIds.contains(nodeId);
    }

    public int setValue(Identifier valueId, int amount) {
        this.internalValues.put(valueId, amount);
        return amount;
    }

    public int incrementValue(Identifier valueId, int amount) {
        int newValue = this.internalValues.getInt(valueId) + amount;
        this.internalValues.put(valueId, newValue);
        return newValue;
    }

    public int getValue(Identifier valueId) {
        return this.internalValues.getInt(valueId);
    }

    /**
     * Returns the raw stored stage progress of a node (1-based, see class docs).
     * 0 = node not started, N = stages 0..N-2 completed and stage N-1 in progress.
     * Prefer the stage helper methods of this class over reading this value directly.
     */
    public int getNodeStageIndex(Identifier nodeId) {
        return this.nodeStageIndexes.getInt(nodeId);
    }

    /**
     * Sets the raw stored stage progress of a node (1-based, see {@link #getNodeStageIndex(Identifier)}).
     * Used by admin commands to reset or set progress; 0 resets the node to not started.
     */
    public void setNodeStageIndex(Identifier nodeId, int stageIndex) {
        this.nodeStageIndexes.put(nodeId, stageIndex);
    }

    /**
     * Returns true if the node has been started, i.e. its first stage became active.
     */
    public boolean isNodeStarted(Identifier nodeId) {
        return this.getNodeStageIndex(nodeId) != 0;
    }

    /**
     * Starts the node at its first stage if it has not been started yet.
     *
     * @return true if the node was started by this call
     */
    public boolean startNodeIfNotStarted(Identifier nodeId) {
        if (this.isNodeStarted(nodeId)) {
            return false;
        }
        this.setNodeStageIndex(nodeId, 1);
        return true;
    }

    /**
     * Marks the stage that is currently in progress as completed, so the next stage becomes the current one.
     */
    public void completeCurrentStage(Identifier nodeId) {
        this.setNodeStageIndex(nodeId, this.getNodeStageIndex(nodeId) + 1);
    }

    /**
     * Returns the number of completed stages of a node (0 if not started).
     */
    public int completedStageCount(Identifier nodeId) {
        return Math.max(0, this.getNodeStageIndex(nodeId) - 1);
    }

    /**
     * Returns the 0-based index of the stage that is in progress, which is also the index of the next stage to complete.
     * Equals {@link #completedStageCount(Identifier)}.
     */
    public int currentStageIndex(Identifier nodeId) {
        return this.completedStageCount(nodeId);
    }

    /**
     * Returns true if the node has completed the stage with the given 0-based index.
     */
    public boolean isStageCompleted(Identifier nodeId, int stageIndex) {
        return this.completedStageCount(nodeId) > stageIndex;
    }

    /**
     * Resets all research state including stage indexes and granted rewards.
     */
    public void reset() {
        this.factIds.clear();
        this.unlockedNodeIds.clear();
        this.internalValues.clear();
        this.nodeStageIndexes.clear();
        this.rewardedStageIds.clear();
    }

    /**
     * Marks the rewards of a stage as granted. Returns true if they were not granted before.
     */
    public boolean markRewardsGranted(Identifier stageId) {
        return this.rewardedStageIds.add(stageId);
    }

    public Set<Identifier> rewardedStageIds() {
        return Set.copyOf(this.rewardedStageIds);
    }

    public Set<Identifier> factIds() {
        return Set.copyOf(this.factIds);
    }

    public Set<Identifier> unlockedNodeIds() {
        return Set.copyOf(this.unlockedNodeIds);
    }

    public Map<Identifier, Integer> valuesMap() {
        var copy = new HashMap<Identifier, Integer>();
        this.internalValues.forEach((k, v) -> copy.put(k, v));
        return Map.copyOf(copy);
    }

    public Map<Identifier, Integer> nodeStagesMap() {
        var copy = new HashMap<Identifier, Integer>();
        this.nodeStageIndexes.forEach((k, v) -> copy.put(k, v));
        return Map.copyOf(copy);
    }
}
