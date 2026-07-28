/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.networking.SyncResearchDataMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResearchDataManager extends SimpleJsonResourceReloadListener<JsonElement> {

    public static final String FOLDER = Data.RESEARCH_DATA_PATH;

    private static final ResearchDataManager INSTANCE = new ResearchDataManager();

    private ResearchData data = new ResearchData(Set.of(), Set.of(), Set.of(), Set.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
    // Sync authored definitions plus graph membership only; validate() rebuilds all derived indexes client-side.
    private List<ResearchFactDefinition> facts = List.of();
    private List<ResearchNodeDefinition> nodes = List.of();
    private List<ResearchValueDefinition> values = List.of();
    private List<ResearchHookDefinition<?>> hooks = List.of();

    private ResearchDataManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json(FOLDER));
    }

    public static ResearchDataManager get() {
        return INSTANCE;
    }

    public ResearchData data() {
        return this.data;
    }

    public SyncResearchDataMessage getSyncMessage() {
        return new SyncResearchDataMessage(
                this.facts,
                this.nodes,
                this.values,
                this.hooks,
                this.data.graphFactIds(),
                this.data.graphNodeIds(),
                this.data.graphValueIds()
        );
    }

    /**
     * Returns hooks for a specific trigger type that match the given context.
     */
    public <TTarget, TContext extends TriggerContext> List<ResearchHookDefinition<TTarget>> hooksFor(
            TriggerType<TTarget, TContext> type, TContext context) {
        return this.data.hooksFor(type, context);
    }

    /**
     * Returns all hooks for a specific trigger type.
     */
    public <TTarget> List<ResearchHookDefinition<TTarget>> hooksForType(TriggerType<TTarget, ?> type) {
        return this.data.hooksForType(type);
    }

    public void installSyncedData(
            List<ResearchFactDefinition> facts,
            List<ResearchNodeDefinition> nodes,
            List<ResearchValueDefinition> values,
            List<ResearchHookDefinition<?>> hooks,
            Map<Identifier, Set<Identifier>> graphFactIds,
            Map<Identifier, Set<Identifier>> graphNodeIds,
            Map<Identifier, Set<Identifier>> graphValueIds
    ) {
        var syncedFacts = List.copyOf(facts);
        var syncedNodes = List.copyOf(nodes);
        var syncedValues = List.copyOf(values);
        var syncedHooks = List.copyOf(hooks);
        var syncedGraphFactIds = copyGraphMap(graphFactIds);
        var syncedGraphNodeIds = copyGraphMap(graphNodeIds);
        var syncedGraphValueIds = copyGraphMap(graphValueIds);
        var validated = ResearchData.validate(
                syncedFacts,
                syncedNodes,
                syncedValues,
                syncedHooks,
                syncedGraphFactIds,
                syncedGraphNodeIds,
                syncedGraphValueIds
        );

        this.facts = syncedFacts;
        this.nodes = syncedNodes;
        this.values = syncedValues;
        this.hooks = syncedHooks;
        this.data = validated;
    }

    public void onDatapackSyncPacket(SyncResearchDataMessage message) {
        this.installSyncedData(
                message.facts,
                message.nodes,
                message.values,
                message.hooks,
                message.graphFactIds,
                message.graphNodeIds,
                message.graphValueIds
        );
    }

    public void onDatapackSync(ServerPlayer player) {
        if (player.connection.connection.isMemoryConnection()) {
            return;
        }

        Services.NETWORK.sendToSplit(player, this.getSyncMessage());
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        var facts = new ArrayList<ResearchFactDefinition>();
        var nodes = new ArrayList<ResearchNodeDefinition>();
        var values = new ArrayList<ResearchValueDefinition>();
        var hooks = new ArrayList<ResearchHookDefinition<?>>();

        var graphFacts = new HashMap<Identifier, List<Identifier>>();
        var graphNodes = new HashMap<Identifier, List<Identifier>>();
        var graphValues = new HashMap<Identifier, List<Identifier>>();

        var sortedEntries = elements.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Identifier::toString)))
                .toList();

        for (var entry : sortedEntries) {
            var path = entry.getKey().getPath();
            var fileName = path.substring(path.lastIndexOf('/') + 1);

            // Extract graph ID from folder path: strip "generated/" prefix if present
            var folderPath = path.substring(0, path.lastIndexOf('/'));
            if (folderPath.startsWith("generated/")) {
                folderPath = folderPath.substring("generated/".length());
            }
            var graphId = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), folderPath);

            if (fileName.equals("facts")) {
                var parsed = ResearchFactDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                facts.addAll(parsed);
                graphFacts.computeIfAbsent(graphId, k -> new ArrayList<>()).addAll(parsed.stream().map(ResearchFactDefinition::id).toList());
            } else if (fileName.equals("nodes")) {
                var parsed = ResearchNodeDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                nodes.addAll(parsed);
                graphNodes.computeIfAbsent(graphId, k -> new ArrayList<>()).addAll(parsed.stream().map(ResearchNodeDefinition::id).toList());
            } else if (fileName.equals("values")) {
                var parsed = ResearchValueDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                values.addAll(parsed);
                graphValues.computeIfAbsent(graphId, k -> new ArrayList<>()).addAll(parsed.stream().map(ResearchValueDefinition::id).toList());
            } else if (fileName.equals("hooks")) {
                hooks.addAll(ResearchHookDefinition.CODEC.listOf().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
            }
        }

        // Build immutable graph index maps
        var graphFactIds = new HashMap<Identifier, Set<Identifier>>();
        graphFacts.forEach((k, v) -> graphFactIds.put(k, Set.copyOf(v)));
        var graphNodeIds = new HashMap<Identifier, Set<Identifier>>();
        graphNodes.forEach((k, v) -> graphNodeIds.put(k, Set.copyOf(v)));
        var graphValueIds = new HashMap<Identifier, Set<Identifier>>();
        graphValues.forEach((k, v) -> graphValueIds.put(k, Set.copyOf(v)));

        this.installSyncedData(facts, nodes, values, hooks, graphFactIds, graphNodeIds, graphValueIds);
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
