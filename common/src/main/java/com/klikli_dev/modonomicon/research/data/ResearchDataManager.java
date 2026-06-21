/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.google.gson.JsonElement;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResearchDataManager extends SimpleJsonResourceReloadListener<JsonElement> {

    public static final String FOLDER = Data.RESEARCH_DATA_PATH;

    private static final ResearchDataManager INSTANCE = new ResearchDataManager();

    private ResearchData data = new ResearchData(Set.of(), Set.of(), Set.of(), Set.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());

    private ResearchDataManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json(FOLDER));
    }

    public static ResearchDataManager get() {
        return INSTANCE;
    }

    public ResearchData data() {
        return this.data;
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

    @Override
    protected void apply(Map<Identifier, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        var facts = new ArrayList<ResearchFactDefinition>();
        var nodes = new ArrayList<ResearchNodeDefinition>();
        var values = new ArrayList<ResearchValueDefinition>();
        var hooks = new ArrayList<ResearchHookDefinition<?>>();

        var graphFacts = new HashMap<Identifier, List<Identifier>>();
        var graphNodes = new HashMap<Identifier, List<Identifier>>();
        var graphValues = new HashMap<Identifier, List<Identifier>>();

        for (var entry : elements.entrySet()) {
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

        this.data = ResearchData.validate(facts, nodes, values, hooks, graphFactIds, graphNodeIds, graphValueIds);
    }
}
