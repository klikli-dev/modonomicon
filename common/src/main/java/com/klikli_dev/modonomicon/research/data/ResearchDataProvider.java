/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResearchDataProvider implements DataProvider {
    private final PackOutput output;

    public ResearchDataProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path root = this.output.getOutputFolder();
        return CompletableFuture.allOf(
                DataProvider.saveStable(cache, list(ResearchFactDefinition.CODEC, List.of(
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/condition_root_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/condition_level_1_viewed"))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/facts.json")),
                DataProvider.saveStable(cache, list(ResearchNodeDefinition.CODEC, List.of(
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/condition_level_1"), List.of(Identifier.parse("modonomicon:demo/condition_root_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/condition_level_2"), List.of(Identifier.parse("modonomicon:demo/condition_level_1_viewed")))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/nodes.json")),
                DataProvider.saveStable(cache, list(ResearchHookDefinition.CODEC, List.of(
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/condition_root_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/condition_root"), Identifier.parse("modonomicon:demo/condition_root_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/condition_level_1_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/condition_level_1"), Identifier.parse("modonomicon:demo/condition_level_1_viewed"))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/hooks.json"))
        );
    }

    private static <T> JsonElement list(com.mojang.serialization.Codec<T> codec, List<T> values) {
        JsonArray array = new JsonArray();
        for (T value : values) {
            array.add(codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow());
        }
        return array;
    }

    @Override
    public String getName() {
        return "Research Data";
    }
}
