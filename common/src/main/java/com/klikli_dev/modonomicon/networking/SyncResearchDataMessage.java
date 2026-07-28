/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SyncResearchDataMessage implements Message {

    public static final Type<SyncResearchDataMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "sync_research_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncResearchDataMessage> STREAM_CODEC = CustomPacketPayload.codec(SyncResearchDataMessage::encode, SyncResearchDataMessage::new);

    public List<ResearchFactDefinition> facts;
    public List<ResearchNodeDefinition> nodes;
    public List<ResearchValueDefinition> values;
    public List<ResearchHookDefinition<?>> hooks;
    public Map<Identifier, Set<Identifier>> graphFactIds;
    public Map<Identifier, Set<Identifier>> graphNodeIds;
    public Map<Identifier, Set<Identifier>> graphValueIds;

    public SyncResearchDataMessage(
            List<ResearchFactDefinition> facts,
            List<ResearchNodeDefinition> nodes,
            List<ResearchValueDefinition> values,
            List<ResearchHookDefinition<?>> hooks,
            Map<Identifier, Set<Identifier>> graphFactIds,
            Map<Identifier, Set<Identifier>> graphNodeIds,
            Map<Identifier, Set<Identifier>> graphValueIds
    ) {
        this.facts = List.copyOf(facts);
        this.nodes = List.copyOf(nodes);
        this.values = List.copyOf(values);
        this.hooks = List.copyOf(hooks);
        this.graphFactIds = copyGraphMap(graphFactIds);
        this.graphNodeIds = copyGraphMap(graphNodeIds);
        this.graphValueIds = copyGraphMap(graphValueIds);
    }

    public SyncResearchDataMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        writeList(buf, this.facts, ResearchFactDefinition.STREAM_CODEC);
        writeList(buf, this.nodes, ResearchNodeDefinition.STREAM_CODEC);
        writeList(buf, this.values, ResearchValueDefinition.STREAM_CODEC);
        writeList(buf, this.hooks, ResearchHookDefinition.STREAM_CODEC);
        writeGraphMap(buf, this.graphFactIds);
        writeGraphMap(buf, this.graphNodeIds);
        writeGraphMap(buf, this.graphValueIds);
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        this.facts = readList(buf, ResearchFactDefinition.STREAM_CODEC);
        this.nodes = readList(buf, ResearchNodeDefinition.STREAM_CODEC);
        this.values = readList(buf, ResearchValueDefinition.STREAM_CODEC);
        this.hooks = readList(buf, ResearchHookDefinition.STREAM_CODEC);
        this.graphFactIds = readGraphMap(buf);
        this.graphNodeIds = readGraphMap(buf);
        this.graphValueIds = readGraphMap(buf);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ResearchDataManager.get().onDatapackSyncPacket(this);
    }

    private static <T> void writeList(RegistryFriendlyByteBuf buf, List<T> values, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        buf.writeVarInt(values.size());
        for (var value : values) {
            codec.encode(buf, value);
        }
    }

    private static <T> List<T> readList(RegistryFriendlyByteBuf buf, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        int size = buf.readVarInt();
        var values = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            values.add(codec.decode(buf));
        }
        return List.copyOf(values);
    }

    private static void writeGraphMap(RegistryFriendlyByteBuf buf, Map<Identifier, Set<Identifier>> map) {
        buf.writeVarInt(map.size());
        for (var entry : map.entrySet()) {
            Identifier.STREAM_CODEC.encode(buf, entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (var value : entry.getValue()) {
                Identifier.STREAM_CODEC.encode(buf, value);
            }
        }
    }

    private static Map<Identifier, Set<Identifier>> readGraphMap(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        var map = new LinkedHashMap<Identifier, Set<Identifier>>(size);
        for (int i = 0; i < size; i++) {
            var key = Identifier.STREAM_CODEC.decode(buf);
            int valueCount = buf.readVarInt();
            var values = new LinkedHashSet<Identifier>(valueCount);
            for (int j = 0; j < valueCount; j++) {
                values.add(Identifier.STREAM_CODEC.decode(buf));
            }
            map.put(key, Collections.unmodifiableSet(values));
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Identifier, Set<Identifier>> copyGraphMap(Map<Identifier, Set<Identifier>> source) {
        var copy = new LinkedHashMap<Identifier, Set<Identifier>>();
        source.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Identifier::toString)))
                .forEach(entry -> {
                    LinkedHashSet<Identifier> orderedValues = entry.getValue().stream()
                            .sorted(Comparator.comparing(Identifier::toString))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    copy.put(entry.getKey(), Collections.unmodifiableSet(orderedValues));
                });
        return Collections.unmodifiableMap(copy);
    }
}
