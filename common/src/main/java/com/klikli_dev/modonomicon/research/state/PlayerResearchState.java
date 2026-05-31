/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.Set;

public class PlayerResearchState {

    public static final Codec<PlayerResearchState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.set(Identifier.CODEC).optionalFieldOf("facts", Set.of()).forGetter(PlayerResearchState::factIds),
            Codecs.set(Identifier.CODEC).optionalFieldOf("unlocked_nodes", Set.of()).forGetter(PlayerResearchState::unlockedNodeIds)
    ).apply(instance, PlayerResearchState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResearchState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Set<Identifier> factIds;
    private final Set<Identifier> unlockedNodeIds;

    public PlayerResearchState() {
        this(Set.of(), Set.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds) {
        this.factIds = new ObjectOpenHashSet<>(factIds);
        this.unlockedNodeIds = new ObjectOpenHashSet<>(unlockedNodeIds);
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

    public void reset() {
        this.factIds.clear();
        this.unlockedNodeIds.clear();
    }

    public Set<Identifier> factIds() {
        return Set.copyOf(this.factIds);
    }

    public Set<Identifier> unlockedNodeIds() {
        return Set.copyOf(this.unlockedNodeIds);
    }
}
