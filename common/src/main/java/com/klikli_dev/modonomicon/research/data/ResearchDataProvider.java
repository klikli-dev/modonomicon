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
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/condition_level_1_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/condition_level_2_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/formatting_basic_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/formatting_advanced_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/features_recipe_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/features_spotlight_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/features_component_icon_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/features_empty_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/features_image_viewed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/advancement_mine_stone_completed")),
                        new ResearchFactDefinition(Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat_completed"))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/facts.json")),
                DataProvider.saveStable(cache, list(ResearchNodeDefinition.CODEC, List.of(
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/condition_level_1"), List.of(Identifier.parse("modonomicon:demo/condition_root_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/condition_level_2"), List.of(Identifier.parse("modonomicon:demo/condition_level_1_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/formatting_advanced"), List.of(Identifier.parse("modonomicon:demo/formatting_basic_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/formatting_link"), List.of(Identifier.parse("modonomicon:demo/formatting_advanced_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_spotlight"), List.of(Identifier.parse("modonomicon:demo/features_recipe_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_component_icon"), List.of(Identifier.parse("modonomicon:demo/features_spotlight_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_empty"), List.of(Identifier.parse("modonomicon:demo/features_spotlight_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_image"), List.of(Identifier.parse("modonomicon:demo/features_empty_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_custom_icon"), List.of(Identifier.parse("modonomicon:demo/features_image_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_two_parents_root"), List.of(Identifier.parse("modonomicon:demo/condition_root_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/features_two_parents_level_2"), List.of(Identifier.parse("modonomicon:demo/condition_level_2_viewed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/advancement_mine_stone"), List.of(Identifier.parse("modonomicon:demo/advancement_mine_stone_completed"))),
                        new ResearchNodeDefinition(Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat"), List.of(Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat_completed")))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/nodes.json")),
                DataProvider.saveStable(cache, list(ResearchHookDefinition.CODEC, List.of(
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/condition_root_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/condition_root"), Identifier.parse("modonomicon:demo/condition_root_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/condition_level_1_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/condition_level_1"), Identifier.parse("modonomicon:demo/condition_level_1_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/condition_level_2_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/condition_level_2"), Identifier.parse("modonomicon:demo/condition_level_2_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/formatting_basic_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:formatting/basic"), Identifier.parse("modonomicon:demo/formatting_basic_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/formatting_advanced_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:formatting/advanced"), Identifier.parse("modonomicon:demo/formatting_advanced_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/features_recipe_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/recipe"), Identifier.parse("modonomicon:demo/features_recipe_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/features_spotlight_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/spotlight"), Identifier.parse("modonomicon:demo/features_spotlight_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/features_component_icon_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/component_icon"), Identifier.parse("modonomicon:demo/features_component_icon_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/features_empty_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/empty"), Identifier.parse("modonomicon:demo/features_empty_viewed")),
                        new ResearchHookDefinition(Identifier.parse("modonomicon:demo/features_image_viewed_once"), ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE, Identifier.parse("modonomicon:features/image"), Identifier.parse("modonomicon:demo/features_image_viewed"))
                )), root.resolve("data/modonomicon/modonomicon/research/demo/hooks.json")),
                DataProvider.saveStable(cache, list(AdvancementResearchHookDefinition.CODEC, List.of(
                        new AdvancementResearchHookDefinition(
                                Identifier.parse("modonomicon:demo/advancement_mine_stone_completed_hook"),
                                Identifier.parse("minecraft:story/mine_stone"),
                                Identifier.parse("modonomicon:demo/advancement_mine_stone_completed")
                        ),
                        new AdvancementResearchHookDefinition(
                                Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat_completed_hook"),
                                Identifier.parse("minecraft:husbandry/ride_a_boat_with_a_goat"),
                                Identifier.parse("modonomicon:demo/advancement_ride_boat_with_goat_completed")
                        )
                )), root.resolve("data/modonomicon/modonomicon/research/demo/advancement_hooks.json"))
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
