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

import java.util.Map;
import java.util.Set;

public class PlayerResearchState {

    public static final Codec<PlayerResearchState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.set(Identifier.CODEC).optionalFieldOf("facts", Set.of()).forGetter(PlayerResearchState::factIds),
            Codecs.set(Identifier.CODEC).optionalFieldOf("unlocked_nodes", Set.of()).forGetter(PlayerResearchState::unlockedNodeIds),
            Codecs.identifierIntMap().optionalFieldOf("values", Map.of()).forGetter(PlayerResearchState::valuesMap)
    ).apply(instance, PlayerResearchState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResearchState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Set<Identifier> factIds;
    private final Set<Identifier> unlockedNodeIds;
    private final Object2IntOpenHashMap<Identifier> internalValues;

    public PlayerResearchState() {
        this(Set.of(), Set.of(), Map.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds) {
        this(factIds, unlockedNodeIds, Map.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values) {
        this.factIds = new ObjectOpenHashSet<>(factIds);
        this.unlockedNodeIds = new ObjectOpenHashSet<>(unlockedNodeIds);
        this.internalValues = new Object2IntOpenHashMap<>(values);
    }

    public boolean grantFact(Identifier factId) {
        return this.factIds.add(factId);
    }

    public boolean hasFact(Identifier factId) {
        return this.factIds.contains(factId);
    }

    public boolean unlockNode(Identifier nodeId) {
        return this.unlockedNodeIds.add(nodeId);
    }

    public boolean isNodeUnlocked(Identifier nodeId) {
        return this.unlockedNodeIds.contains(nodeId);
    }

    public int incrementValue(Identifier valueId, int amount) {
        int newValue = this.internalValues.getInt(valueId) + amount;
        this.internalValues.put(valueId, newValue);
        return newValue;
    }

    public int getValue(Identifier valueId) {
        return this.internalValues.getInt(valueId);
    }

    public void reset() {
        this.factIds.clear();
        this.unlockedNodeIds.clear();
        this.internalValues.clear();
    }

    public Set<Identifier> factIds() {
        return Set.copyOf(this.factIds);
    }

    public Set<Identifier> unlockedNodeIds() {
        return Set.copyOf(this.unlockedNodeIds);
    }

    public Map<Identifier, Integer> valuesMap() {
        var copy = new java.util.HashMap<Identifier, Integer>();
        this.internalValues.forEach((k, v) -> copy.put(k, v));
        return Map.copyOf(copy);
    }
}
